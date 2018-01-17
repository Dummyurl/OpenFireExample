package com.jj.investigation.openfire.activity;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.ChatAdapter;
import com.jj.investigation.openfire.bean.IMGroup;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.retrofit.RetrofitService;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.DateUtils;
import com.jj.investigation.openfire.utils.GsonUtils;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.view.VoiceRecordButton;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 群聊界面
 * 以后要与单聊界面改成同一个
 * Created by ${R.js} on 2018/1/17.
 */

public class GroupChatActivity extends AppCompatActivity implements
        VoiceRecordButton.OnVoiceRecordListener, View.OnClickListener, MessageListener {

    private RetrofitService api;
    private TextView tv_title;
    private EditText et_input_sms;
    private ListView lv_message_chat;
    private XMPPTCPConnection connection;
    // 消息集合
    private List<MyMessage> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    // 接收消息
    private static final int MESSAGE_RECEIVE = 0;
    private MultiUserChat chat;
    private BroadcastReceiver receiver;
    private IMGroup groupInfo;
    // 房间的jid--房间的唯一标识
    private String jid;
    // 当前用户--用户的jid
    private String currentUser;


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECEIVE:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
    }

    private void initView() {
        TextView tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_input_sms = (EditText) findViewById(R.id.et_input_sms);
        lv_message_chat = (ListView) findViewById(R.id.lv_message_chat);
        VoiceRecordButton btn_voice_record = (VoiceRecordButton) findViewById(R.id.btn_voice_record);
        btn_voice_record.setOnVoiceRecordListener(this);
    }

    private void initData() {

        api = RetrofitUtil.createApi();
        connection = XmppManager.getConnection();
        groupInfo = (IMGroup) getIntent().getSerializableExtra("groupInfo");
        jid = groupInfo.getJid();
        name = XmppStringUtils.parseLocalpart(jid);
        tv_title.setText(name);
        currentUser = connection.getUser();

        adapter = new ChatAdapter(this, messageList);
        lv_message_chat.setAdapter(adapter);

        // 获取群聊会话
        final MultiUserChatManager chatManager = MultiUserChatManager.getInstanceFor(connection);
        chat = chatManager.getMultiUserChat(jid);
        chat.addMessageListener(this);
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
     * 发送消息的点击事件
     * 这里发送的都是文本消息
     */
    public void clickSendMessage(View v) {
        try {
            final String content = et_input_sms.getText().toString().trim();
            // 发送消息(该消息只用来在本地显示)
            final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                    DateUtils.newDate(), MyMessage.OprationType.Send.getType());

            messageList.add(localMessage);
            adapter.notifyDataSetChanged();

            // 要发送的消息，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
            final MyMessage remoteMessage = new MyMessage(currentUser, jid, content,
                    DateUtils.newDate(), MyMessage.OprationType.Receiver.getType());
            chat.sendMessage(remoteMessage.toJson());
            et_input_sms.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("发送消息异常：", e.toString());
        }
    }

    @Override
    public void onRecordEnd(File recordFile, long duration) {
        final String content = et_input_sms.getText().toString().trim();
        // 发送消息(该消息只用来在本地显示)
        final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                recordFile.getName(), duration);
        // 根据决定路径来找录音文件播放
        localMessage.setFileLocalUrl(recordFile.getAbsolutePath());
        Logger.e("绝对路径：" + recordFile.getAbsolutePath());

        messageList.add(localMessage);
        adapter.notifyDataSetChanged();

        // 要发送的消息(文本)，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
        final MyMessage remoteMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Receiver.getType(),
                recordFile.getName(), duration);

        // 发送消息
        try {
            chat.sendMessage(remoteMessage.toJson());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息的监听：
     * 群组聊天，自己也会接收到自己的消息，需要过滤掉
     * 从消息的具体消息中可以看到有一个send字段，如果这个send字段是自己，
     * 则在接收消息的时候可以直接过滤掉。具体的消息在body字段中，可以把body
     * 这个字符串转成自定义的MyMessage对象，从中获取send内容。
     * 不能根据message.getFrom()来判断，因为getFrom获取的是群的jid，不是
     * 发送该消息的用户的jid。
     */
    @Override
    public void processMessage(Message message) {

        final String body = message.getBody();
        final MyMessage myMessage = GsonUtils.getGsonInstance().fromJson(body, MyMessage.class);
        if (myMessage == null) return;
        final String send = myMessage.getSend();
        if (!send.substring(0, send.indexOf("@")).equals(currentUser.substring(0, currentUser.indexOf("@")))) {
            final String json = message.getBody();
            final MyMessage receiveMessage = GsonUtils.getGsonInstance().fromJson(json, MyMessage.class);
            messageList.add(receiveMessage);
            handler.sendEmptyMessage(MESSAGE_RECEIVE);
        }
    }

}
