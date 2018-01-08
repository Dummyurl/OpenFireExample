package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.AutoEditText;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 注册
 * Created by ${R.js} on 2017/12/15.
 */

public class RegisterActivity extends AppCompatActivity {

    private AutoEditText et_account;
    private AutoEditText et_pwd;
    private AutoEditText et_email;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("注册");
        loadingDialog = new LoadingDialog(this);

        et_account = (AutoEditText) findViewById(R.id.et_account);
        et_pwd = (AutoEditText) findViewById(R.id.et_pwd);
        et_email = (AutoEditText) findViewById(R.id.et_email);
    }

    public void register(View v) {
        RegisterTask loginTask = new RegisterTask();
        loginTask.execute(et_account.getText().toString(), et_pwd.getText()
                .toString(), et_email.getText().toString());

        requestRegister();
    }

    /**
     * Openfire注册
     */
    class RegisterTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog != null) {
                loadingDialog.showDialog("注册中...");
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // 新版本注册
            XMPPTCPConnection connection = XmppManager.getConnection();
            AccountManager accountManager = AccountManager
                    .getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            try {
                // 附加属性
                if (!(params[2] == null || params[2] == "")) {
                    accountManager.createAccount(params[0], params[1]);
                } else {
                    Map<String, String> attributes = new HashMap<>();
                    attributes.put("email", params[2]);
                    accountManager.createAccount(params[0], params[1], attributes);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("注册异常：", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (loadingDialog != null) {
                loadingDialog.hideDialog();
            }
            if (result) {
                ToastUtils.showShortToastSafe("注册成功");
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                ToastUtils.showShortToastSafe("注册失败");
            }
        }
    }

    /**
     * 自己平台的注册
     */
    public void requestRegister() {
        final String jid = et_account.getText().toString().trim() + "@" + XmppManager.SERVICE_NAME;
        RetrofitUtil.createApi().regist(et_account.getText().toString().trim(),
                et_pwd.getText().toString().trim(), et_email.getText().toString().trim(), jid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Log.e("注册失败", e.toString());
                    }

                    @Override
                    public void onNext(ServletData data) {

                        if (data.getCode() == 200) {
                            Log.e("注册成功", data.toString());
                        } else {
                            Log.e("注册失败", data.toString());
                        }
                    }
                });
    }
}
