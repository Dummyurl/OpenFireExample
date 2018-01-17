package com.jj.investigation.openfire.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.smack.GroupManager;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * 加入群组页面
 * Created by ${R.js} on 2018/1/16.
 */

public class GroupJoinActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_chat_room_name;
    private EditText et_chat_room_pwd;
    private EditText et_chat_room_nickname;
    private String groupName;
    private String groupPwd;
    private String groupnickName;
    private LoadingDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);

        initView();
    }

    private void initView() {
        dialog = new LoadingDialog(this);
        TextView tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("创建群聊");

        et_chat_room_name = (EditText) findViewById(R.id.et_chat_room_name);
        et_chat_room_pwd = (EditText) findViewById(R.id.et_chat_room_pwd);
        et_chat_room_nickname = (EditText) findViewById(R.id.et_chat_room_nickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;
        }
    }

    /**
     * 加入群组的点击事件
     * @param btn
     */
    public void groupJoin(View btn) {

        groupName = et_chat_room_name.getText().toString();
        groupPwd = et_chat_room_pwd.getText().toString();
        groupnickName = et_chat_room_nickname.getText().toString();
        // 创建房间(群组)
        new ChatRoomJoinTask().execute();
        dialog.showDialog("请稍后...");
    }

    /**
     * 加群
     */
    class ChatRoomJoinTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            XMPPTCPConnection connection = XmppManager.getConnection();
            try {
                GroupManager.groupJoin(connection, groupName, groupPwd, groupnickName);
                GroupManager.collectGroups(connection, groupName, groupPwd, groupnickName);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("加入群组失败：" + e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                ToastUtils.showShortToast("加入群组成功");
                finish();
            } else {
                ToastUtils.showShortToast("加入群组失败");
            }
            dialog.hideDialog();
        }

    }
}
