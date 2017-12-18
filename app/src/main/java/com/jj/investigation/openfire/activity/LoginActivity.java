package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.AutoEditText;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

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
        LoginTask loginTask = new LoginTask();
        loginTask.execute(et_username.getText().toString(), et_password
                .getText().toString());
    }

    /**
     * 注册的点击事件
     */
    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * 登录任务
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.showDialog("正在登录...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            XMPPTCPConnection connection = XmppManager.getConnection();
            try {
                connection.login(params[0], params[1]);
                connection.sendStanza(new Presence(Presence.Type.available));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("登录失败：", e.toString());
                if ("org.jivesoftware.smack.SmackException$AlreadyLoggedInException: Client is already logged in".equals(e.toString())) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean loginStatus) {
            loadingDialog.hideDialog();
            if (loginStatus) {
                ToastUtils.showShortToastSafe("登录成功");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                ToastUtils.showShortToastSafe("登录失败");
            }
        }
    }

}
