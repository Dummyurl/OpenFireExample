package com.jj.investigation.openfire.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.view.chatbottom.JSChatBottomView;

/**
 * 聊天测试界面
 * Created by ${R.js} on 2018/1/19.
 */
public class ChatTestActivity extends AppCompatActivity {

    private JSChatBottomView jschat_bottom_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test);

        jschat_bottom_view = (JSChatBottomView) findViewById(R.id.jschat_bottom_view);

        initListener();
    }

    private void initListener() {

    }


}
