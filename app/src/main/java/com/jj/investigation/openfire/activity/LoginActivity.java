package com.jj.investigation.openfire.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * 登录
 * Created by ${R.js} on 2017/12/15.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    public void login(View v) {
        LoginTask loginTask = new LoginTask();
        loginTask.execute(et_username.getText().toString(), et_password
                .getText().toString());
    }

    /**
     * 登录任务
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            XMPPTCPConnection connection = XmppManager.getConnection();
            try {
                // 登录
                connection.login(params[0], params[1]);
                connection.sendStanza(new Presence(Presence.Type.available));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("失败原因", e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                ToastUtils.showShortToastSafe("登录成功");
            } else {
                ToastUtils.showShortToastSafe("登录失败");
            }
        }
    }
}
