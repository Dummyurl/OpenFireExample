package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.MyJoinGroupsAdapter;
import com.jj.investigation.openfire.bean.IMGroup;
import com.jj.investigation.openfire.smack.GroupManager;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.List;

/**
 * 群组管理类--UI
 * Created by ${R.js} on 2018/1/16.
 */

public class GroupManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv_chat_room;
    private MyJoinGroupsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manager);
        initView();
        initData();
    }

    private void initView() {
        TextView tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("群组管理");

        lv_chat_room = (ListView) findViewById(R.id.lv_chat_room);
    }

    private void initData() {
        adapter = new MyJoinGroupsAdapter(this);
        lv_chat_room.setAdapter(adapter);
        new MyJoinGroupsLoadTask().execute();
    }

    /**
     * 创建群组的点击事件
     */
    public void groupCreate(View view) {
        startActivity(new Intent(this, GroupCreateActivity.class));
    }

    /**
     * 加入群组的点击事件
     */
    public void groupJoin(View view) {
        startActivity(new Intent(this, GroupJoinActivity.class));
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
     * 获取我加入的群组
     */
    class MyJoinGroupsLoadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            final XMPPTCPConnection connection = XmppManager.getConnection();
            try {
                final List<IMGroup> groupList = GroupManager.getJoinGroupList(connection);
                if (groupList != null && groupList.size() > 0) {
                    adapter.setGroups(groupList);
                } else {
                    return false;
                }
            } catch (Exception e) {
                Logger.e("获取我加入的群组异常：" + e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                adapter.notifyDataSetChanged();
            } else {
                ToastUtils.showLongToast("没有群组");
            }
        }
    }
}
