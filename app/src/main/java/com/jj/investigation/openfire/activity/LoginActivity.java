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
import com.jj.investigation.openfire.bean.User;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.Constants;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ShareValue;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.AutoEditText;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登录
 * Created by ${R.js} on 2017/12/15.
 */

public class LoginActivity extends AppCompatActivity {

    private AutoEditText et_username;
    private AutoEditText et_password;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        loadingDialog = new LoadingDialog(this);
    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("登录");
        et_username = (AutoEditText) findViewById(R.id.et_account);
        et_password = (AutoEditText) findViewById(R.id.et_pwd);
    }

    /**
     * 登录的点击事件
     */
    public void login(View v) {
        XmppManager.setConnectionNull();
        requestLogin();
    }

    /**
     * 去注册的点击事件
     */
    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * 登录任务：openfire登录
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean loginStatus = true;
            XMPPTCPConnection connection = XmppManager.getConnection();
            Logger.e("登录的状态：" + connection.isConnected());
            try {
                connection.login(params[0], params[1]);
                connection.sendStanza(new Presence(Presence.Type.available));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("登录失败：", e.toString());
                loginStatus = false;
            }
            return loginStatus;
        }

        @Override
        protected void onPostExecute(Boolean loginStatus) {
            loadingDialog.hideDialog();
            if (loginStatus) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                startActivity(new Intent(LoginActivity.this, ChatTestActivity.class));
                finish();
            } else {
                ToastUtils.showShortToastSafe("登录失败");
            }
        }
    }


    /**
     * 自己平台的登录
     */
    private void requestLogin() {
        loadingDialog.showDialog("正在登录...");
        RetrofitUtil.createApi().login(et_username.getText().toString().trim(),
                et_password.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData<User>>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Log.e("登录失败", e.toString());
                    }

                    @Override
                    public void onNext(ServletData<User> data) {

                        if (data.getCode() == 200) {
                            // 自己平台登录成功，开始OpenFire的登录
                            LoginTask loginTask = new LoginTask();
                            loginTask.execute(et_username.getText().toString(), et_password
                                    .getText().toString());
                            // 存储id以及jid
                            User user = data.getData();
                            ShareValue.getInstance(LoginActivity.this).putStringValue(Constants.SP_UID,
                                    String.valueOf(user.getId()));
                            ShareValue.getInstance(LoginActivity.this).putStringValue(Constants.SP_JID,
                                    String.valueOf(user.getJid()));
                            Log.e("登录成功", data.toString());
                        } else {
                            loadingDialog.hideDialog();
                            Log.e("登录失败", data.toString());
                        }
                    }
                });
    }
}
