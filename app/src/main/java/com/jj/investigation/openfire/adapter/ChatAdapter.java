package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.view.CircleImageView;

import java.util.List;

/**
 * 聊天
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<MyMessage> messageList;
    private LayoutInflater inflater;

    // 文本（代表14中布局类型中的文本布局类型）
    private static final int MESSAGE_TYPE_TXT_RECE = 0;
    private static final int MESSAGE_TYPE_TXT_SENT = 1;
    // 语音
    private static final int MESSAGE_TYPE_VOICE_SENT = 2;
    private static final int MESSAGE_TYPE_VOICE_RECE = 3;
    // 图片
    private static final int MESSAGE_TYPE_IMAGE_SENT = 4;
    private static final int MESSAGE_TYPE_IMAGE_RECE = 5;
    // 位置
    private static final int MESSAGE_TYPE_LOCATION_SENT = 6;
    private static final int MESSAGE_TYPE_LOCATION_RECV = 7;
    // 视频
    private static final int MESSAGE_TYPE_VIDEO_SENT = 8;
    private static final int MESSAGE_TYPE_VIDEO_RECE = 9;
    // 文件
    private static final int MESSAGE_TYPE_FILE_SENT = 10;
    private static final int MESSAGE_TYPE_FILE_RECE = 11;
    // 表情
    private static final int MESSAGE_TYPE_EXPRESSION_SENT = 12;
    private static final int MESSAGE_TYPE_EXPRESSION_RECE = 13;

    public ChatAdapter(Context context, List<MyMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public MyMessage getItem(int position) {
        return messageList == null ? null : messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        final MyMessage message = getItem(position);
        if (message == null) return  -1;
        if (message.getMessageType() == MyMessage.MessageType.Text.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ? MESSAGE_TYPE_TXT_SENT :MESSAGE_TYPE_TXT_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.Voice.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ? MESSAGE_TYPE_VOICE_SENT :MESSAGE_TYPE_VOICE_RECE;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return MyMessage.MessageType.values().length * 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MyMessage message = getItem(position);
        if (convertView == null) {
            if (getItemViewType(position) == MESSAGE_TYPE_TXT_SENT) {
                convertView = inflater.inflate(R.layout.item_chat_text_sent, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_TXT_RECE) {
                convertView = inflater.inflate(R.layout.item_chat_text_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_VOICE_RECE) {
                convertView = inflater.inflate(R.layout.item_chat_voice_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_VOICE_SENT) {
                convertView = inflater.inflate(R.layout.item_chat_voice_sent, parent, false);
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (message == null) {
            message = new MyMessage("", "", "", "", 0);
        }

        // 设置布局的内容
        if (message.getMessageType() == MyMessage.MessageType.Text.getType()) {
            holder.tv_message_text.setText(message.getContent());
        } else if (message.getMessageType() == MyMessage.MessageType.Voice.getType()){
            holder.tv_chat_time.setText(message.getVoiceRecordTime() + "");
        }
        holder.tv_chat_time.setText(message.getData());

        return convertView;
    }


    class ViewHolder {
        // 时间
        TextView tv_chat_time;
        // 头像
        CircleImageView iv_head_view;
        // 文本内容
        TextView tv_message_text;
        // 语音时长
        TextView tv_voice_duration;
        // 语音未读时显示的小红点
        View view_read_status;

        public ViewHolder(View convertView) {
            if (convertView == null) return;
            tv_chat_time = (TextView) convertView.findViewById(R.id.tv_chat_time);
            iv_head_view = (CircleImageView) convertView.findViewById(R.id.iv_head_view);
            tv_message_text = (TextView) convertView.findViewById(R.id.tv_message_text);
            tv_voice_duration = (TextView) convertView.findViewById(R.id.tv_voice_duration);
            view_read_status = convertView.findViewById(R.id.view_read_status);
        }
    }
}
