package com.jj.investigation.openfire.bean;

import com.jj.investigation.openfire.utils.GsonUtils;

/**
 * 通讯的消息体
 * Created by ${R.js} on 2017/12/19.
 */

public class MyMessage {

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
    // 发送文件的文件名称
    private String fileName;
    // 文件在手机本地的路径
    private String fileLocalUrl;
    // 语音文件的时长
    private long voiceRecordTime;

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

    public String getFileLocalUrl() {
        return fileLocalUrl;
    }

    public void setFileLocalUrl(String fileLocalUrl) {
        this.fileLocalUrl = fileLocalUrl;
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
                '}';
    }

    /**
     * 把消息转成json
     */
    public String toJson() {
        return GsonUtils.getGsonInstance().toJson(this);
    }
}
