package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;

/**
 * 群组管理类--UI
 * Created by ${R.js} on 2018/1/16.
 */

public class GroupManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv_chat_room;

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

    }

    /**
     * 创建群组
     */
    public void groupCreate(View view) {
        startActivity(new Intent(this, GroupCreateActivity.class));
    }

    /**
     * 加入群组
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
}
