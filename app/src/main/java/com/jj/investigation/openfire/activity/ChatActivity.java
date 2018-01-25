package com.jj.investigation.openfire.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jj.investigation.openfire.utils.FileManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.VoiceRecordButton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 聊天界面（单聊）：
 * 因为单聊和群聊有很多不同的地方，为了方便看单聊和群聊做成了两个页面
 * 使用当前用户和对方的jid是为了聊天使用，OpenFire只认识jid。
 * 根据jid从自己的平台查询对应的用户，拿到用户在自己平台的信息，这个是在聊天界面显示时使用
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatActivity extends AppCompatActivity implements ChatManagerListener, ChatMessageListener,
        VoiceRecordButton.OnVoiceRecordListener, FileTransferListener, View.OnClickListener {

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
    // 文件监听接收类，只支持单聊，群聊不支持
    private FileTransferManager fileTransferManager;
    // 接收消息
    private static final int MESSAGE_RECEIVE = 0;
    // 刷新UI
    private static final int MESSAGE_REFRESH = 1;

    // 对方正在输入......
    private static final int MESSAGE_COMPOSING = 2;
    // 对方停止输入......
    private static final int MESSAGE_PAUSED = 3;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECEIVE:
                    adapter.notifyDataSetChanged();
                    break;
                case MESSAGE_REFRESH:
                    adapter.notifyDataSetChanged();
                    break;
                case MESSAGE_COMPOSING:
                    tv_title.setText("正在输入...");
                    break;
                case MESSAGE_PAUSED:
                    tv_title.setText(name);
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
        editTextListener();
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
        // 获取对方的jid以及名称
        jid = getIntent().getStringExtra("jid");
        name = XmppStringUtils.parseLocalpart(jid);
        tv_title.setText(name);

        queryUserInfoAccordingToJid();

        adapter = new ChatAdapter(this, messageList);
        lv_message_chat.setAdapter(adapter);

        // 获取连接
        connection = XmppManager.getConnection();
        // 获取当前用户jid
        currentUser = connection.getUser();
        // 获取聊天管理器
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        // 创建聊天会话
        chat = chatManager.createChat(jid);
        // 会话创建成功的监听
        chatManager.addChatListener(this);

        // 获取OpenFire的文件管理器并添加上传文件的监听
        fileTransferManager = FileTransferManager.getInstanceFor(connection);
        fileTransferManager.addFileTransferListener(this);
    }

    /**
     * 输入框的监听
     * 如果输入内容在增加，则发送正在输入的状态，如果删除内容，则不发送消息
     */
    private void editTextListener() {

        et_input_sms.addTextChangedListener(new TextWatcher() {

            // 输入的字符串长度
            private int count = 0;
            private ChatStateThread thread;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;
        }
    }

    /**
     * 根据jid查询用户的信息：
     * 自己和对方的信息
     * 这个要改了，聊天记录要和用户信息一起返回，只是用一个接口即可
     */
    private void queryUserInfoAccordingToJid() {
        final String jids = Utils.getJid() + "-" + jid;
        api.getChatUsersInfo(jids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData<ArrayList<User>>>() {
                    @Override
                    public void onCompleted() {}
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
     * 接收消息的监听：对方和自己的都会接收
     */
    @Override
    public void processMessage(Chat chat, Message message) {

        // 1.收到输入状态
        final ExtensionElement extension = message.getExtension("http://jabber.org/protocol/chatstates");
        if (extension != null) {
            Logger.e("extension = " + extension.getElementName());
        }
        // TODO 奇怪的问题：当发送消息后，extension的状态应该是active才对，但实际上extension为null
        if (extension == null) {
            final MyMessage myMessage = new Gson().fromJson(message.getBody(), MyMessage.class);
            messageList.add(myMessage);
            handler.sendEmptyMessage(MESSAGE_RECEIVE);
        }
        if (extension.getElementName().equals("composing")) { // 对方正在输入
            handler.sendEmptyMessage(MESSAGE_COMPOSING);
        } else if (extension.getElementName().equals("paused")) { // 对方暂停输入
            handler.sendEmptyMessage(MESSAGE_PAUSED);
        } else if (extension.getElementName().equals("active")) { // 对方点击发送消息
            final MyMessage myMessage = new Gson().fromJson(message.getBody(), MyMessage.class);
            if (myMessage != null) {
                messageList.add(myMessage);
                handler.sendEmptyMessage(MESSAGE_RECEIVE);
            }
        } else { // 自己的消息
            android.os.Message obtainMessage = handler.obtainMessage(
                    MESSAGE_RECEIVE, message.getBody());
            handler.sendMessage(obtainMessage);
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
     * 发送消息的点击事件
     * 这里发送的都是文本消息
     */
    public void clickSendMessage(View v) {
        try {
            final String content = et_input_sms.getText().toString().trim();
            // 1.发送消息(该消息只用来在本地显示)
            final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                    DateUtils.newDate(), MyMessage.OprationType.Send.getType());

            messageList.add(localMessage);
            adapter.notifyDataSetChanged();

            // 2.要发送的消息，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
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
        api.addChatRecord(et_input_sms.getText().toString().trim(), "text", from_uid, to_uid, DateUtils.newDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServletData>() {
                    @Override
                    public void onCompleted() {
                    }

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

    /**
     * 录音结束的监听：结束后向服务器发送文件
     *
     * @param recordFile 录音文件
     * @param duration   录音文件的时长
     */
    @Override
    public void onRecordEnd(File recordFile, long duration) {
        final String content = et_input_sms.getText().toString().trim();
        // 发送消息(该消息只用来在本地显示)
        final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                recordFile.getName(), duration);
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
            // 发送语音
            final OutgoingFileTransfer outgoingFileTransfer = fileTransferManager.
                    createOutgoingFileTransfer(jid + "/Smack");
            outgoingFileTransfer.sendFile(recordFile, remoteMessage.toJson()); // 后面参数是对文件的描述
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OpenFire上传文件的监听
     * 只要对方一发送文件，这里就可以监听到，在这里进行文件的接收，也就是文件的下载
     */
    @Override
    public void fileTransferRequest(FileTransferRequest request) {
        final IncomingFileTransfer accept = request.accept();

        // 下载文件：
        // 1.获取文件
        final File file = new File(FileManager.createFile("voice"), accept.getFileName());

        try {
            // 2.下载文件
            accept.recieveFile(file);
            Thread.sleep(3000);
            // 3.判断文件是否下载成功:complete--下载成功，其他为失败
            if (accept.getStatus() == FileTransfer.Status.complete) {
                updataMessageState(file, MyMessage.MessageState.Sucess);
                android.os.Message message = handler.obtainMessage(MESSAGE_REFRESH, file.getName());
                handler.sendMessage(message);
            } else {
                updataMessageState(file, MyMessage.MessageState.Error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("下载文件出错：" + e.toString());
        }
    }

    /**
     * 更新adapter中文件下载的状态
     * 循环所有消息下载的文件，如果文件如当前下载的文件相同，则设置当前消息的文件下载状态
     *
     * @param file         下载的文件
     * @param messageState 下载文件的状态
     */
    public void updataMessageState(File file, MyMessage.MessageState messageState) {
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final MyMessage message = adapter.getItem(i);
            if (message.getFileName().equals(file.getName())) {
                message.setMessageState(messageState.getType());
            }
        }
    }

    /**
     * 监听输入框输入的线程
     */
    class ChatStateThread extends Thread {

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
