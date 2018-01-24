package com.jj.investigation.openfire.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.ChatAdapter;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.bean.ServletData;
import com.jj.investigation.openfire.bean.User;
import com.jj.investigation.openfire.impl.ChatPictureSelectedListener;
import com.jj.investigation.openfire.impl.ChatTextSendListener;
import com.jj.investigation.openfire.retrofit.RetrofitService;
import com.jj.investigation.openfire.retrofit.RetrofitUtil;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.DateUtils;
import com.jj.investigation.openfire.utils.FileManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.VoiceRecordButton;
import com.jj.investigation.openfire.view.chatbottom.JSChatBottomView;
import com.yanzhenjie.album.AlbumFile;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
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
 *
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatActivity2 extends AppCompatActivity implements ChatManagerListener, ChatMessageListener,
        VoiceRecordButton.OnVoiceRecordListener, FileTransferListener, View.OnClickListener {

    private TextView tv_title;
    private ListView lv_message_chat;
    private JSChatBottomView jschat_bottom_view;

    private String name;
    private String jid;
    private XMPPTCPConnection connection;
    private Chat chat;
    private String currentUser;
    private String from_uid;
    private String to_uid;
    private RetrofitService api;
    // 输入框内容
    private String content;
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
                    ToastUtils.showLongToast("文件下载成功");
                    Logger.e("没走啊");
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
        setContentView(R.layout.activity_chat2);
        initView();
        initData();
        chatCallBackListener();
    }

    private void initView() {
        TextView tv_left = (TextView) findViewById(R.id.tv_left);
        tv_left.setVisibility(View.VISIBLE);
        tv_left.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_message_chat = (ListView) findViewById(R.id.lv_message_chat);
        jschat_bottom_view = (JSChatBottomView) findViewById(R.id.jschat_bottom_view);
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
        Logger.e("jschat_bottom_view = " + jschat_bottom_view + ", chat = " + chat);
        jschat_bottom_view.setChatManager(chat);
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
     * JSChatBottomView的一些监听：发送消息、选择图片等
     */
    private void chatCallBackListener() {

        // 1.选择图片的监听
        jschat_bottom_view.setChatPictureSelectedListener(new ChatPictureSelectedListener() {
            @Override
            public void pictureSelected(ArrayList<AlbumFile> albumFiles) {
                ToastUtils.showShortToastSafe("选择了图片：" + albumFiles.get(0).getName());
                sendImg(albumFiles);
            }
        });

        // 2.发送文本的监听
        jschat_bottom_view.setChatTextSendListener(new ChatTextSendListener() {
            @Override
            public void textSend(String content) {
                ChatActivity2.this.content = content;
                sendTxt(content);
            }
        });
    }

    /**
     * 发送图片
     */
    private void sendImg(ArrayList<AlbumFile> albumFiles) {
        // 发送消息(该消息只用来在本地显示)
        final String path = albumFiles.get(0).getPath();
        final File file = new File(path);

        final MyMessage localMessage = new MyMessage(currentUser, jid,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                MyMessage.MessageType.Image.getType(), path);

        messageList.add(localMessage);
        adapter.notifyDataSetChanged();

        // 要发送的消息(语音)，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
        final MyMessage remoteMessage = new MyMessage(currentUser, jid,
                DateUtils.newDate(), MyMessage.OprationType.Receiver.getType(),
                MyMessage.MessageType.Image.getType(), path);

        // 发送消息
        try {
            chat.sendMessage(remoteMessage.toJson());
            // 发送语音
            final OutgoingFileTransfer outgoingFileTransfer = fileTransferManager.
                    createOutgoingFileTransfer(jid + "/Smack");
            outgoingFileTransfer.sendFile(file, remoteMessage.toJson()); // 后面参数是对文件的描述
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文本消息
     */
    private void sendTxt(String content) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("发送消息异常：", e.toString());
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
        Logger.e("接收到消息：" + message.toString());
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
            return;
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
     * 向后台加添一条聊天记录
     *
     * @param from_uid 当前用户的user_id
     * @param to_uid   接收消息的用户的user_id
     */
    private void pushRecord(String from_uid, String to_uid) {
        api.addChatRecord(content, "text", from_uid, to_uid, DateUtils.newDate())
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
        // 发送消息(该消息只用来在本地显示)
        final MyMessage localMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                recordFile.getPath(), duration);
        // 根据决定路径来找录音文件播放
        localMessage.setFileLocalUrl(recordFile.getAbsolutePath());
        Logger.e("绝对路径：" + recordFile.getAbsolutePath());

        messageList.add(localMessage);
        adapter.notifyDataSetChanged();

        // 要发送的消息(语音)，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
        final MyMessage remoteMessage = new MyMessage(currentUser, jid, content,
                DateUtils.newDate(), MyMessage.OprationType.Receiver.getType(),
                recordFile.getPath(), duration);

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
        Logger.e("开始下载文件：" + accept.getFileName());
        // 下载文件：
        // 1.获取文件
        File file = null;

        try {
            if (accept.getFileName().contains("voice")) {
                file = new File(FileManager.createFile("voice"), accept.getFileName());
            } else {
                file = new File(FileManager.createFile("file"), accept.getFileName());
            }
            Logger.e("xiazai hou: = " + file.getPath());
            // 2.下载文件
            accept.recieveFile(file);
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
        MyMessage message;
        for (int i = 0; i < count; i++) {
            message = adapter.getItem(i);
            Logger.e("message.tostring = " + message.toString());
            Logger.e("file.getName() = " + file.getName() + ", message = " + message.getFileName());
            Logger.e("fff = " + message.getFileName().contains(file.getName()));
            if (message.getFileName().contains(file.getName())) {
                // 更新消息的绝对路径（之前只是）
                message.setFileName(file.getPath());
                Logger.e("草泥马：" + message.getFileName());
                message.setMessageState(messageState.getType());
            }
        }
    }
}
