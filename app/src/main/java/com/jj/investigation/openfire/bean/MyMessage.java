package com.jj.investigation.openfire.bean;

import com.jj.investigation.openfire.utils.GsonUtils;

/**
 * 通讯的消息体
 * Created by ${R.js} on 2017/12/19.
 */

public class MyMessage {

    public MyMessage() {

    }

    /**
     * 消息操作者类型：发送者还是接收者
     */
    public enum OprationType {
        Send(0),
        Receiver(1);

        private int type;

        private OprationType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /**
     * 消息类型：文本、图片、语音等
     */
    public enum MessageType {
        // 文本消息
        Text(0),
        // 图片消息
        Image(1),
        // 文件消息
        File(2),
        // 视频消息
        Video(3),
        // 语音消息
        Voice(4),
        // 位置消息
        Location(5);
        private int type;

        private MessageType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /**
     * 消息的发送状态
     */
    public enum MessageState {
        // 发送成功
        Sucess(0),
        // 发送中
        Progress(1),
        // 发送失败
        Error(2);
        private int type;

        private MessageState(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }


    // 发送者
    private String send;
    // 接收者
    private String receiver;
    // 消息内容
    private String content;
    // 消息的时间
    private String data;
    // 发送类型：接收者还是发送者
    private int oprationType = OprationType.Send.getType();
    // 消息的类型：文本、语音......
    private int messageType = MessageType.Text.getType();
    // 消息的状态
    private int messageState;
    // 发送文件的文件名称(其实后来这个当做文件的url了，不管是本地的url还是网路的url，因为做demo图简单，以后要要区分开来)
    private String fileName;
    // 网络的url：也可以当做本地的用
    private String fileUrl;
    // 文件的大小
    private String fileSize;
    // 语音文件的时长
    private long voiceRecordTime;
    // 地位位置--经度
    private String longitude;
    // 地位位置--纬度
    private String latitude;
    // 地理位置信息
    private String address;


    /**
     * 文本消息构造方法
     */
    public MyMessage(String send, String receiver, String content, String data, int oprationType) {
        this.send = send;
        this.receiver = receiver;
        this.content = content;
        this.data = data;
        this.oprationType = oprationType;
        this.messageType = MessageType.Text.getType();
    }



    /**
     * 语音消息构造方法
     */
    public MyMessage(String send, String receiver, String content, String data, int oprationType,
                     String fileName, long voiceRecordTime) {
        this.send = send;
        this.receiver = receiver;
        this.content = content;
        this.data = data;
        this.oprationType = oprationType;
        this.messageType = MessageType.Voice.getType();
        this.fileName = fileName;
        this.voiceRecordTime = voiceRecordTime;
    }

    /**
     * 图片消息构造方法
     */
    public MyMessage(String send, String receiver, String data, int oprationType, int messageType,
                     String fileName) {
        this.send = send;
        this.receiver = receiver;
        this.data = data;
        this.oprationType = oprationType;
        this.messageType = messageType;
        this.fileName = fileName;
    }

    /**
     * 文件消息构造方法
     */
    public MyMessage(String send, String receiver, String data, int oprationType, int messageType,
                     String fileName, String fileUrl, String fileSize) {
        this.send = send;
        this.receiver = receiver;
        this.data = data;
        this.oprationType = oprationType;
        this.messageType = messageType;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }


    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getOprationType() {
        return oprationType;
    }

    public void setOprationType(int oprationType) {
        this.oprationType = oprationType;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getVoiceRecordTime() {
        return voiceRecordTime;
    }

    public void setVoiceRecordTime(long voiceRecordTime) {
        this.voiceRecordTime = voiceRecordTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "send='" + send + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", data='" + data + '\'' +
                ", oprationType=" + oprationType +
                ", messageType=" + messageType +
                ", messageState=" + messageState +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", voiceRecordTime=" + voiceRecordTime +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    /**
     * 把消息转成json
     */
    public String toJson() {
        return GsonUtils.getGsonInstance().toJson(this);
    }
}
