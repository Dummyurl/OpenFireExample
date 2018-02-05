package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.net.Uri;
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
import com.jj.investigation.openfire.view.chatbottom.JSChatBottomView;
import com.jj.investigation.openfire.view.other.VoiceRecordButton;
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
 * 聊天界面：
 * 发送语音、文件、图片这三个文件类型的消息需要改成自己的方法，不使用Smack提供的方法，原理如同群聊
 * 界面的发送语音过程。最终发送所有文件类型的消息都转变成发送普通消息，但是文件类型的消息的内容是文件在
 * 自己服务器的存储地址，发送过去之后，由对方根据url解析下载文件
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatActivity extends AppCompatActivity implements ChatManagerListener, ChatMessageListener,
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
    // OpenFire接收消息提醒的扩展
    private static final String EXTENSION = "http://jabber.org/protocol/chatstates";
    // 接收消息
    private static final int MESSAGE_RECEIVE = 0;
    // 刷新UI
    private static final int MESSAGE_REFRESH = 1;
    // 对方正在输入...
    private static final int MESSAGE_COMPOSING = 2;
    // 对方停止输入...
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
                sendImg(albumFiles);
            }
        });

        // 2.发送文本的监听
        jschat_bottom_view.setChatTextSendListener(new ChatTextSendListener() {
            @Override
            public void textSend(String content) {
                ChatActivity.this.content = content;
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
     * 发送文件
     * @param file 要发送的文件
     */
    private void sendFile(File file, String fileSize) {
        // 发送消息(该消息只用来在本地显示)
        final MyMessage localMessage = new MyMessage(currentUser, jid,
                DateUtils.newDate(), MyMessage.OprationType.Send.getType(),
                MyMessage.MessageType.File.getType(), file.getName(), file.getPath(), fileSize);

        messageList.add(localMessage);
        adapter.notifyDataSetChanged();


        // 发送远程消息
        final MyMessage remoteMessage = new MyMessage(currentUser, jid,
                DateUtils.newDate(), MyMessage.OprationType.Receiver.getType(),
                MyMessage.MessageType.File.getType(), file.getName(), file.getPath(), fileSize);

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
        MyMessage localMessage = null;
        try {
            // 1.发送消息(该消息只用来在本地显示)
            localMessage = new MyMessage(currentUser, jid, content,
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
     * 发送地理位置消息
     */
    private void sendLocation(double latitude, double longitude, String address) {
        try {
            // 1.发送消息(该消息只用来在本地显示)
            final MyMessage localMessage = new MyMessage();
            localMessage.setSend(currentUser);
            localMessage.setReceiver(jid);
            localMessage.setData(DateUtils.newDate());
            localMessage.setOprationType(MyMessage.OprationType.Send.getType());
            localMessage.setMessageType(MyMessage.MessageType.Location.getType());
            localMessage.setAddress(address);
            localMessage.setLatitude(String.valueOf(latitude));
            localMessage.setLongitude(String.valueOf(longitude));

            messageList.add(localMessage);
            adapter.notifyDataSetChanged();

            // 2.要发送的消息，发送的消息需要别人来接收，所以发送时OprationType的值应该为Receiver而不是send
            final MyMessage remoteMessage = new MyMessage();
            remoteMessage.setSend(currentUser);
            remoteMessage.setReceiver(jid);
            remoteMessage.setData(DateUtils.newDate());
            remoteMessage.setOprationType(MyMessage.OprationType.Receiver.getType());
            remoteMessage.setMessageType(MyMessage.MessageType.Location.getType());
            remoteMessage.setAddress(address);
            remoteMessage.setLatitude(String.valueOf(latitude));
            remoteMessage.setLongitude(String.valueOf(longitude));
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
        final ExtensionElement extension = message.getExtension(EXTENSION);
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
        } else if (extension.getElementName().equals("active")) { // 对方点击发送消息（现在这里一直为null）
            final MyMessage myMessage = new Gson().fromJson(message.getBody(), MyMessage.class);
            if (myMessage != null) {
                messageList.add(myMessage);
                handler.sendEmptyMessage(MESSAGE_RECEIVE);
            }
        } else { // 自己的消息
            Logger.e("这里应该永远都不走");
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
        Logger.e("语音保存路径：" + recordFile.getAbsolutePath());

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
            if (accept.getFileName().contains("voice")) { // 语音文件
                file = new File(FileManager.createFile("voice"), accept.getFileName());
            } else { // 其他文件：图片、普通文件等，语音也是文件，这里做区分，只是保存的目录区分开而已
                file = new File(FileManager.createFile("file"), accept.getFileName());
            }
            // 2.下载文件：该方法内部又开了一个子线程
            accept.recieveFile(file);
            // 因为上面方法是在一个新的线程中，所以不会马上进入第3步，用循环来判断文件下载的状态，状态只需要
            // 判断结果状态即可，中间不管是初始化、数据流传输中等都不用管，只要结果状态即可。从Openfire的代码
            // 中可以看到结果有下面3个，其中任何一个状态发生，则结束循环
            while (true) {
                Thread.sleep(200);
                if (accept.getStatus() == FileTransfer.Status.error ||
                        accept.getStatus() == FileTransfer.Status.complete ||
                        accept.getStatus() == FileTransfer.Status.cancelled) {
                    break;
                }
            }
            // 3.判断文件是否下载成功:complete--下载成功，其他为失败
            if (accept.getStatus() == FileTransfer.Status.complete) {
                updataMessageState(file, MyMessage.MessageState.Sucess);
                android.os.Message message = handler.obtainMessage(MESSAGE_RECEIVE, file.getName());
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
            if (message.getFileName().contains(file.getName())) {
                // 更新消息的绝对路径（之前只是）
                message.setFileName(file.getPath());
                message.setMessageState(messageState.getType());
            }
        }
    }

    /**
     * 接收系统返回的文件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ToastUtils.showShortToastSafe("找不到数据");
            return;
        }
        Logger.e("返回的数据：" + data.toString());
        switch (requestCode) {
            case JSChatBottomView.FILE_SELECT_CODE:
                dealFileData(resultCode, data);
                break;
            case JSChatBottomView.LOCATION_SELECT_CODE:
                dealLocationData(data);
                break;
        }
    }

    /**
     * 处理返回的位置信息
     */
    private void dealLocationData(Intent data) {
        final double latitude = data.getDoubleExtra("latitude", 0);
        final double longitude = data.getDoubleExtra("longitude", 0);
        final String address = data.getStringExtra("address");
        sendLocation(latitude, longitude, address);
    }

    /**
     * 处理返回的file文件
     */
    private void dealFileData(int resultCode, Intent data) {
        if (resultCode == -1) {
            final Uri uri = data.getData();
            if (uri != null) {
                if (uri.getScheme().equals("file") || uri.getScheme().equals("content")) {
                    String path = uri.getEncodedPath();
                    path = path.replaceAll("external_files", "storage/emulated/0");
                    final File file = new File(path);
                    sendFile(file, FileManager.getFileSize(file));
                } else {
                    ToastUtils.showShortToastSafe("不支持该格式");
                }
            }
        }
    }
}
