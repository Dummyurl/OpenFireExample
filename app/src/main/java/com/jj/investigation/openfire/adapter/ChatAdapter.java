package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.smack.RecordManager;
import com.jj.investigation.openfire.utils.FileManager;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.CircleImageView;

import java.io.File;
import java.util.List;

/**
 * 聊天adapter
 * Created by ${R.js} on 2017/12/19.
 */

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<MyMessage> messageList;
    private LayoutInflater inflater;
    private MediaPlayer mediaPlayer;

    // 文本（代表14种布局类型中的文本布局类型）
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
        mediaPlayer = new MediaPlayer();
        inflater = LayoutInflater.from(context);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放完毕重置
                mediaPlayer.reset();
            }
        });
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
        if (message == null) return -1;
        if (message.getMessageType() == MyMessage.MessageType.Text.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ? MESSAGE_TYPE_TXT_SENT : MESSAGE_TYPE_TXT_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.Voice.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ? MESSAGE_TYPE_VOICE_SENT : MESSAGE_TYPE_VOICE_RECE;
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
        if (convertView == null) {
            if (getItemViewType(position) == MESSAGE_TYPE_TXT_SENT) { // 文本
                convertView = inflater.inflate(R.layout.item_chat_text_sent, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_TXT_RECE) {
                convertView = inflater.inflate(R.layout.item_chat_text_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_VOICE_RECE) { // 语音
                convertView = inflater.inflate(R.layout.item_chat_voice_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_VOICE_SENT) {
                convertView = inflater.inflate(R.layout.item_chat_voice_sent, parent, false);
            }
            holder = new ViewHolder(convertView);
            if (convertView != null) {
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyMessage message = getItem(position);
        // 设置布局的内容
        if (message.getMessageType() == MyMessage.MessageType.Text.getType()) { // 文本
            holder.tv_message_text.setText(message.getContent());
        } else if (message.getMessageType() == MyMessage.MessageType.Voice.getType()) { // 语音
            holder.tv_voice_duration.setText(message.getVoiceRecordTime() + "''");
            // 语音的点击事件
            holder.layout_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (message.getMessageState() == MyMessage.MessageState.Sucess.getType()) {
                        Logger.e("mymessage = " + message);
                        final String filePath = FileManager.searchFile(message.getFileName(), "voice");
                        Logger.e("文件的路径1：" + filePath);
                        final File file = new File(message.getFileName());
                        Logger.e("exists = " + file.exists());
                        if (file.exists()) {
                            try {
                                RecordManager.playAudio(context, message.getFileName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        if (!Utils.isNull(filePath)) {
//                            try {
//                                RecordManager.playAudio(context, filePath);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
                    } else {
                        ToastUtils.showLongToast("语音文件异常");
                    }
                }
            });
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
        // 消息发送失败
        ImageView iv_voice_error;
        // 整个语音的布局
        LinearLayout layout_voice;

        public ViewHolder(View convertView) {
            if (convertView == null) return;
            tv_chat_time = (TextView) convertView.findViewById(R.id.tv_chat_time);
            iv_head_view = (CircleImageView) convertView.findViewById(R.id.iv_head_view);
            tv_message_text = (TextView) convertView.findViewById(R.id.tv_message_text);
            tv_voice_duration = (TextView) convertView.findViewById(R.id.tv_voice_duration);
            view_read_status = convertView.findViewById(R.id.view_read_status);
            iv_voice_error = (ImageView) convertView.findViewById(R.id.iv_voice_error);
            layout_voice = (LinearLayout) convertView.findViewById(R.id.layout_voice);
        }
    }
}
