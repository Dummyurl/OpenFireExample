package com.jj.investigation.openfire.view.chatitem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.ChatAdapter;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.impl.ChatMsgErrorClickListener;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.other.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * 会话聊天View
 * Created by ${R.js} on 2018/2/1.
 */

public abstract class BaseChatItem extends RelativeLayout implements View.OnClickListener {

    private TextView tv_chat_time;
    private CircleImageView iv_head_view;
    private FrameLayout fl_chat_item_container;
    private ImageView iv_chat_item_error;
    private ProgressBar pb_chat_item;

    protected Context context;
    protected Activity activity;
    // 消息数据
    protected MyMessage message;
    protected ChatAdapter adapter;
    private ChatMsgErrorClickListener listener;


    public BaseChatItem(Context context, MyMessage message, ChatAdapter adapter,
                        ChatMsgErrorClickListener listener) {
        super(context);
        this.context = context;
        this.message = message;
        this.adapter = adapter;
        this.listener = listener;
        this.activity = (Activity) context;
        initView();
        initData();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(
                message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                        R.layout.item_chat_base_sent : R.layout.item_chat_base_rece, this);
        tv_chat_time = (TextView) findViewById(R.id.tv_chat_time);
        iv_head_view = (CircleImageView) findViewById(R.id.iv_head_view);
        fl_chat_item_container = (FrameLayout) findViewById(R.id.fl_chat_item_container);
        iv_chat_item_error = (ImageView) findViewById(R.id.iv_chat_item_error);
        pb_chat_item = (ProgressBar) findViewById(R.id.pb_chat_item);
        iv_chat_item_error.setOnClickListener(this);
        iv_head_view.setOnClickListener(this);
        fl_chat_item_container.addView(initChildView());
    }

    /**
     * 设置父类的数据：
     * 1）用户头像、昵称（昵称先不做，之后添加）
     * 2）信息接收与上传的状态：上传中、下载中、上传接收失败
     */
    protected void initData() {
        // 1.设置头像
        if (!Utils.isNull(message.getUserImg())) {
            if (message.getOprationType() == MyMessage.OprationType.Send.getType()) {
                Picasso.with(context).load(message.getUserImg()).placeholder(R.drawable.head_default)
                        .error(R.drawable.head_default).into(iv_head_view);
            } else {
                Picasso.with(context).load(message.getUserImg()).placeholder(R.drawable.miniq_logo)
                        .error(R.drawable.head_default).into(iv_head_view);
            }
        } else {
            if (message.getOprationType() == MyMessage.OprationType.Send.getType()) {
                iv_head_view.setImageResource(R.drawable.head_default);
            } else {
                iv_head_view.setImageResource(R.drawable.miniq_logo);
            }
        }

        // 2.设置发送与接收的状态
        if (message.getMessageState() == MyMessage.MessageState.Progress.getType()) {
            iv_chat_item_error.setVisibility(View.GONE);
            pb_chat_item.setVisibility(View.VISIBLE);
        } else if (message.getMessageState() == MyMessage.MessageState.Error.getType()) {
            iv_chat_item_error.setVisibility(View.VISIBLE);
            pb_chat_item.setVisibility(View.GONE);
        } else {
            iv_chat_item_error.setVisibility(View.GONE);
            pb_chat_item.setVisibility(View.GONE);
        }
    }


    /**
     * 子类添加自己的布局View，子类的findViewByID也在该方法中进行
     */
    protected abstract View initChildView();

    /**
     * 子类设置具体的消息数据
     */
    protected abstract void initChildData();


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head_view:

                break;
            case R.id.iv_chat_item_error:
                if (listener != null)
                    listener.msgErrorClick(message);
                break;
        }
    }
}
