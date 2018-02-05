package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.impl.ChatTextSendListener;
import com.jj.investigation.openfire.view.other.VoiceRecordButton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

/**
 * 聊天界面--输入文字、录音的布局
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatInputView extends LinearLayout implements View.OnClickListener {

    private Context context;
    private Button btn_more;
    private Button btn_send;
    private EditText et_message;
    private VoiceRecordButton btn_voice_record;
    private ImageView iv_emoj;
    private ChatTextSendListener chatTextSendListener;
    private Chat chat;

    public JSChatInputView(Context context) {
        this(context, null);
    }

    public JSChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        editTextListener();
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.js_chat_input_view, this);
        btn_voice_record = (VoiceRecordButton) findViewById(R.id.btn_voice_record);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_more = (Button) findViewById(R.id.btn_more);
        btn_send = (Button) findViewById(R.id.btn_send);
        iv_emoj = (ImageView) findViewById(R.id.iv_emoj);
        btn_send.setOnClickListener(this);
    }

    /**
     * 设置聊天会话
     */
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    /**
     * 输入框的监听
     * 如果输入内容在增加，则发送正在输入的状态，如果删除内容，则不发送消息
     */
    private void editTextListener() {

        et_message.addTextChangedListener(new TextWatcher() {

            // 输入的字符串长度
            private int count = 0;
            private ChatStateThread thread;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 发送按钮和加号的切换
                if (!TextUtils.isEmpty(s)) {
                    btn_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                } else {
                    btn_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }


                if (this.count < s.length()) {
                    if (thread == null || !thread.isRunning) {
                        thread = new ChatStateThread();
                        thread.start();
                        sendMessageState(ChatState.composing);
                    }
                    thread.setOldTime(System.currentTimeMillis());
                }
                this.count = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 设置emoji点击事件的监听
     */
    public void setOnEmojMenuClickListener(View.OnClickListener listener) {
        iv_emoj.setOnClickListener(listener);
    }

    /**
     * 设置更多的点击事件的监听
     */
    public void setOnPlusMenuClickListener(View.OnClickListener listener) {
        btn_more.setOnClickListener(listener);
    }

    /**
     * 输入框的点击事件：点击后隐藏下面的menu
     */
    public void setOnKeyboardClickListener(View.OnClickListener listener) {
        et_message.setOnClickListener(listener);
    }

    /**
     * 发送消息的点击事件回调
     */
    public void setOnTxtSendListener(View.OnClickListener listener) {
        btn_send.setOnClickListener(listener);
    }

    /**
     * 返回输入框Edittext
     */
    public EditText getEt_message() {
        return et_message;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_send:
                break;
        }
    }

    /**
     * 发送输入状态的消息：
     * 也是通过chat发送了一条消息，但是该消息的内容就是用户的输入状态而已
     */
    private void sendMessageState(ChatState chatState) {
        final Message message = new Message();
        // 添加状态消息
        message.addExtension(new ChatStateExtension(chatState));
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听输入框输入的线程
     */
    public class ChatStateThread extends Thread {

        // 上一次输入的时间
        private long oldTime;
        // 最新一次输入的时间
        private long newTime;
        // 默认两次输入间隔时间1秒内有效
        private static final long TIME_INTERVAL = 1000;
        // 控制线程的结束
        private boolean isRunning = true;


        public ChatStateThread() {
            oldTime = System.currentTimeMillis();
            newTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    // 每隔200毫秒判断一次时间间隔
                    Thread.sleep(200);
                    if (newTime - oldTime > TIME_INTERVAL) {
                        isRunning = false;
                        sendMessageState(ChatState.paused);
                    }
                    newTime = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 重置旧的时间
         */
        public void setOldTime(long oldTime) {
            this.oldTime = oldTime;
        }
    }

}
