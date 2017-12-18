package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.AutoEditText;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.util.HashMap;
import java.util.Map;

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

    public void login(View v) {
        RegisterTask loginTask = new RegisterTask();
        loginTask.execute(et_account.getText().toString(), et_pwd.getText()
                .toString(), et_email.getText().toString());
    }

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
                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put("email", params[2]);
                accountManager.createAccount(params[0], params[1], attributes);
            } catch (Exception e) {
                e.printStackTrace();
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
            } else {
                ToastUtils.showShortToastSafe("注册失败");
            }
        }
    }
}
