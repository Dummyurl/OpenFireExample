package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

/**
 * 注册
 * Created by ${R.js} on 2017/12/15.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        loadingDialog = new LoadingDialog(this);
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    /**
     * 注册
     */
    public void register(View v) {
        RegisterTask registerTask = new RegisterTask();
        registerTask.execute(et_username.getText().toString(), et_password
                .getText().toString());
    }

    class RegisterTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.showDialog("正在注册...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //新版本注册
            final XMPPTCPConnection connection = XmppManager.getConnection();
            final AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            try {
                accountManager.createAccount(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("注册失败：", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean registerStatus) {
            super.onPostExecute(registerStatus);
            loadingDialog.hideDialog();
            if (registerStatus) {
                ToastUtils.showShortToastSafe("注册成功");
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            } else {
                ToastUtils.showShortToastSafe("注册失败");
            }
        }
    }
}
