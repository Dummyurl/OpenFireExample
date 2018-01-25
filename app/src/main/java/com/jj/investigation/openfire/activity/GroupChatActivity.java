package com.jj.investigation.openfire.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.dao.ChatDao;
import com.jj.investigation.openfire.impl.NetRequestRefreshListener;
import com.jj.investigation.openfire.retrofit.RequestBodyUtils;
import com.jj.investigation.openfire.service.DownLoadService;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.DateUtils;
import com.jj.investigation.openfire.utils.GsonUtils;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.VoiceRecordButton;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 群聊界面:
 * 因为与单聊还有很多不同，所以干脆写了两个页面，方便查看，如果非得放在一个Activity也可以，多做一些判断即可，
 * 此时做只是先为了能赶快完成功能，做出demo
 * 群组发送语音与单聊发送语音不一样，单聊发送语音有专门针对文件接收的监听，但群聊没有。群聊语音的实现原理是：
 * 录音完毕后先把语音文件上传到服务器（自己的服务器），上传成功后返回语音文件在服务器的存放地址，然后把地址
 * 组装成需要OpenFire发送的消息JavaBean对象，然后发送这个语音文件，此时的发送是通过OpenFire的服务器发送的，
 * 发送后，群组接收到消息，消息中带有语音在服务器存放的URL，在群里点击消息时通过URL下载语音文件。
 * Created by ${R.js} on 2018/1/17.
 */

public class GroupChatActivity extends AppCompatActivity implements
        VoiceRecordButton.OnVoiceRecordListener, View.OnClickListener, MessageListener, NetRequestRefreshListener {

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
    // 录音文件
    private File recordFile;
    // 录音时长
    private long duration;


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECEIVE:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private String name;
    private ChatDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
        initService();
        initReceiver();
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
        dao = new ChatDao(this, this);
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

    /**
     * 启动文件下载的服务
     */
    private void initService() {
        startService(new Intent(this, DownLoadService.class));
    }


    /**
     * 注册文件下载成功的广播
     */
    private void initReceiver() {
        receiver = new DownloadSuccessReceiver();
        final IntentFilter filter = new IntentFilter(DownLoadService.FILE_DOWNLOAD_SUCCESS);
        registerReceiver(receiver, filter);
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
            if (Utils.isNull(content)) return;
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
    public void onRecordEnd(final File recordFile, final long duration) {
        this.recordFile = recordFile;
        this.duration = duration;
        final String content = et_input_sms.getText().toString().trim();
        // 发送消息(该消息只用来在本地显示)
        final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                recordFile.getPath(), duration);
        Logger.e("绝对路径：" + recordFile.getAbsolutePath());

        messageList.add(localMessage);
        adapter.notifyDataSetChanged();

        // 先向后台写入语音文件，写入成功再通过OpenFire发送消息
        final Map<String, RequestBody> fileMap = new HashMap<>();
        fileMap.put("type", RequestBodyUtils.toRequestBody("voice"));
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), recordFile);
        fileMap.put("file" + "\"; filename=\"" + recordFile.getName() + "", fileRequestBody);
        dao.sendFile(fileMap);

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
        System.out.println("接收到消息：" + message.toString());
        final String body = message.getBody();
        final MyMessage myMessage = GsonUtils.getGsonInstance().fromJson(body, MyMessage.class);
        if (myMessage == null) return;
        final String send = myMessage.getSend();
        if (!send.substring(0, send.indexOf("@")).equals(currentUser.substring(0, currentUser.indexOf("@")))) {
            final String json = message.getBody();
            final MyMessage receiveMessage = GsonUtils.getGsonInstance().fromJson(json, MyMessage.class);
            messageList.add(receiveMessage);

            // 如果是语音消息，则直接下载
            if (receiveMessage.getMessageType() == MyMessage.MessageType.Voice.getType()) {
                // 使用广播让Activity和Service通信
                Intent intent = new Intent(DownLoadService.FILE_DOWNLOAD);
                // 语音文件的下载地址
                intent.putExtra("loadUrl", receiveMessage.getFileName());
                // 语音文件下载好后保存到本地的二级目录
                intent.putExtra("localUrl", "voice");
                sendBroadcast(intent);
            }

            handler.sendEmptyMessage(MESSAGE_RECEIVE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * 网络访问成功的回调
     *
     * @param data 返回正常的数据
     */
    @Override
    public void onSuccess(ServletData data, int page) {

        // 要发送的消息，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
        final MyMessage remoteMessage = new MyMessage(currentUser, jid,
                et_input_sms.getText().toString().trim(),
                DateUtils.newDate(), MyMessage.OprationType.Receiver.getType(),
                (String) data.getData(), duration);
        try {
            chat.sendMessage(remoteMessage.toJson());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("语音消息异常：" + e.toString());
        }
    }

    @Override
    public void onFailer(String msg, String type, int page) {
        ToastUtils.showLongToast("发送失败");
    }

    /**
     * 文件下载成功的广播
     */
    class DownloadSuccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownLoadService.FILE_DOWNLOAD_SUCCESS)) {
                final String fileName = intent.getStringExtra("fileName");
                updateState(fileName);
            }
        }
    }

    /**
     * 文件下载成功更新数据:
     * 目前更新的是语音消息，一旦语音下载成功，会在本地生成一个文件，fileName为语音的路径，循环所有的消息，
     * 如果语音的网络路径中中的文件名称与本地路径中的文件名称一致，则把网络的语音路径替换为本地下载好的语音
     * 路劲，这样点击后可以直接播放
     * @param fileName 本地语音文件的绝对路径
     */
    private void updateState(String fileName) {
        final int count = adapter.getCount();
        MyMessage message;
        // 本地语音文件的文件名称
        String localFileName;
        // 网络路径的文件的名称
        String netFileName;
        for (int i = 0; i < count; i++) {
            message = adapter.getItem(i);
            if (message != null) {
                localFileName = fileName.substring(fileName.lastIndexOf("/"), fileName.length());
                netFileName = message.getFileName().substring(message.getFileName().lastIndexOf("/"),
                        message.getFileName().length());
                if (localFileName.equals(netFileName)) {
                    message.setFileName(fileName);
                    message.setMessageState(MyMessage.MessageState.Sucess.getType());
                }
            }
        }
    }

}
