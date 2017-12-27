package com.jj.investigation.openfire.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.ChatAdapter;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.bean.User;
import com.jj.investigation.openfire.retrofit.RetrofitService;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.DateUtils;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.RequestBodyUtils;
import com.jj.investigation.openfire.utils.Utils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 聊天界面
 * 使用当前用户和对方的jid是为了聊天使用，OpenFire只认识jid。
 * 根据jid从自己的平台查询对应的用户，拿到用户在自己平台的信息，这个是在聊天界面显示时使用
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatActivity extends AppCompatActivity implements ChatManagerListener, ChatMessageListener {

    private EditText et_input_sms;
    private String name;
    private TextView tv_title;
    private String jid;
    private ListView lv_message_chat;
    private XMPPTCPConnection connection;
    private Chat chat;
    private String currentUser;
    private String from_uid;
    private String to_uid;
    private RetrofitService api;
    // 消息集合
    private List<MyMessage> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private static final int MESSAGE_RECEIVE = 0;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_input_sms = (EditText) findViewById(R.id.et_input_sms);
        lv_message_chat = (ListView) findViewById(R.id.lv_message_chat);
    }

    private void initData() {
        api = RetrofitUtil.createApi();
        // 获取对方的jid以及名称
        jid = getIntent().getStringExtra("jid");
        name = XmppStringUtils.parseLocalpart(jid);
        tv_title.setText(name);

        queryUserInfoAccordingToJid();

        adapter = new ChatAdapter(this, messageList);
        lv_message_chat.setAdapter(adapter);

        // 创建会话
        connection = XmppManager.getConnection();
        // 获取当前用户jid
        currentUser = connection.getUser();
        // 获取聊天管理器
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        // 创建聊天会话
        chat = chatManager.createChat(jid);
        // 会话创建成功的监听
        chatManager.addChatListener(this);
    }

    /**
     * 根据jid查询用户的信息：
     * 自己和对方的信息
     */
    private void queryUserInfoAccordingToJid() {
        final String jids = Utils.getJid() + "-" + jid;
        api.getChatUsersInfo(jids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData<ArrayList<User>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("查询失败", e.toString());
                    }

                    @Override
                    public void onNext(ServletData<ArrayList<User>> servletData) {
                        final ArrayList<User> userList = servletData.getData();
                        handleData(userList);
                        Log.e("查询成功", servletData.toString());
                    }
                });
    }

    /**
     * 获取到用户信息后进行设置
     */
    private void handleData(ArrayList<User> userList) {
        if (userList != null && userList.size() > 0) {
            final User currentUser = userList.get(0);
            final User otherUser = userList.get(1);
            from_uid = currentUser.getId();
            to_uid = otherUser.getId();
            if (!Utils.isNull(otherUser.getNickname())) {
                tv_title.setText(otherUser.getNickname());
            } else {
                tv_title.setText(otherUser.getUsername());
            }
        }
    }

    /**
     * 创建会话的监听
     */
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        // 会话创建成功后创建接收消息的监听
        // 这里需要注意的是：不要使用this.chat.addMessageListener(this)，不然接收不到消息
        chat.addMessageListener(this);
    }

    /**
     * 接收消息的监听
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        String json = message.getBody();
        Gson gson = new Gson();
        MyMessage receiveMessage = gson.fromJson(json, MyMessage.class);
        messageList.add(receiveMessage);
        handler.sendEmptyMessage(MESSAGE_RECEIVE);
    }

    /**
     * 发送消息的点击事件
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
            pushRecord(from_uid, to_uid);
            et_input_sms.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("发送消息异常：", e.toString());
        }
    }

    /**
     * 向后台加添一条聊天记录
     *
     * @param from_uid 当前用户的user_id
     * @param to_uid   接收消息的用户的user_id
     */
    private void pushRecord(String from_uid, String to_uid) {
        final Map<String, RequestBody> map = new HashMap<>();
        map.put("msg", RequestBodyUtils.toRequestBody(et_input_sms.getText().toString().trim()));
        map.put("msg_type", RequestBodyUtils.toRequestBody("text"));
        map.put("from_uid", RequestBodyUtils.toRequestBody(from_uid));
        map.put("to_uid", RequestBodyUtils.toRequestBody(to_uid));
        map.put("send_time", RequestBodyUtils.toRequestBody(DateUtils.newDate()));
        api.addChatRecord(et_input_sms.getText().toString().trim(), "text", from_uid, to_uid, DateUtils.newDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Logger.e("添加消息异常：" + e.toString());
                    }

                    @Override
                    public void onNext(ServletData servletData) {
                        Logger.e("添加消息成功：" + servletData.toString());
                    }
                });

    }
}
