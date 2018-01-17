package com.jj.investigation.openfire.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.GroupManager;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.LoadingDialog;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.util.XmppStringUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建群组
 * Created by ${R.js} on 2018/1/15.
 */

public class GroupCreateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_chat_room_name;
    private EditText et_chat_room_pwd;
    private EditText et_chat_room_desc;
    private LoadingDialog loadingDialog;
    private String name;
    private String pwd;
    private String desc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        initView();
        loadingDialog = new LoadingDialog(this);
    }

    private void initView() {

        TextView tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("创建群聊");

        et_chat_room_name = (EditText) findViewById(R.id.et_chat_room_name);
        et_chat_room_pwd = (EditText) findViewById(R.id.et_chat_room_pwd);
        et_chat_room_desc = (EditText) findViewById(R.id.et_chat_room_desc);
    }

    /**
     * 创建群组的点击事件
     * @param btn
     */
    public void chatRoomCreate(View btn) {
        name = et_chat_room_name.getText().toString().trim();
        pwd = et_chat_room_pwd.getText().toString().trim();
        desc = et_chat_room_desc.getText().toString().trim();
        if (Utils.isNull(name)) {
            ToastUtils.showLongToast("名称不能为空");
            return;
        }
        if (name.length() > 10) {
            ToastUtils.showLongToast("长度不能超过10个字符");
        }
        if (Utils.isNull(pwd)) {
            ToastUtils.showLongToast("密码不能为空");
            return;
        }

        // 创建房间(群组)
        groupCreate();
    }

    /**
     * 在后台数据库创建群组
     */
    private void groupCreate() {
        RetrofitUtil.createApi().createGroup(name, desc, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Logger.e("创建群组失败：" + e.toString());
                        ToastUtils.showLongToast("创建失败");
                        loadingDialog.hideDialog();
                    }

                    @Override
                    public void onNext(ServletData servletData) {
                        Logger.e("创建群组成功：" + servletData.toString());
                        if (servletData.getCode() == 200) {
                            // 现在自己数据库添加成功后再去OpenFire中创建
                            new ChatRoomCreateTask().execute();
                        } else {
                            loadingDialog.hideDialog();
                            ToastUtils.showLongToast(servletData.getMsg());
                        }
                    }
                });
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
     * 从OpenFire创建群组
     */
    public class ChatRoomCreateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.showDialog("群组创建中...");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final XMPPTCPConnection connection = XmppManager.getConnection();
            try {
                // 获取用户的昵称
                final String nickName = XmppStringUtils.parseLocalpart(connection
                        .getUser());
                GroupManager.groupCreate(connection, name, pwd, desc);
                GroupManager.collectGroups(connection, name, pwd, nickName);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("创建群组异常：" + e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                ToastUtils.showLongToast("创建群组成功");
                finish();
            } else {
                ToastUtils.showLongToast("创建群组失败");
            }
            loadingDialog.hideDialog();
        }
    }
}
