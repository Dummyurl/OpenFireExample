package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.activity.JSBaiDuMapActivity;
import com.jj.investigation.openfire.activity.ZoomPictureActivity;
import com.jj.investigation.openfire.bean.MyMessage;
import com.jj.investigation.openfire.smack.RecordManager;
import com.jj.investigation.openfire.utils.BitmapUtils;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.other.CircleImageView;

import java.io.File;
import java.util.ArrayList;
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
    private static final int MESSAGE_TYPE_LOCATION_RECE = 7;
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
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                    MESSAGE_TYPE_TXT_SENT : MESSAGE_TYPE_TXT_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.Voice.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                    MESSAGE_TYPE_VOICE_SENT : MESSAGE_TYPE_VOICE_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.Image.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                    MESSAGE_TYPE_IMAGE_SENT : MESSAGE_TYPE_IMAGE_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.File.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                    MESSAGE_TYPE_FILE_SENT : MESSAGE_TYPE_FILE_RECE;
        }
        if (message.getMessageType() == MyMessage.MessageType.Location.getType()) {
            return message.getOprationType() == MyMessage.OprationType.Send.getType() ?
                    MESSAGE_TYPE_LOCATION_SENT : MESSAGE_TYPE_LOCATION_RECE;
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
            } else if (getItemViewType(position) == MESSAGE_TYPE_IMAGE_SENT) { // 图片
                convertView = inflater.inflate(R.layout.item_chat_img_sent, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_IMAGE_RECE) {
                convertView = inflater.inflate(R.layout.item_chat_img_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_FILE_RECE) { // 文件
                convertView = inflater.inflate(R.layout.item_chat_file_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_FILE_SENT) {
                convertView = inflater.inflate(R.layout.item_chat_file_sent, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_LOCATION_RECE) { // 地理位置
                convertView = inflater.inflate(R.layout.item_chat_location_rece, parent, false);
            } else if (getItemViewType(position) == MESSAGE_TYPE_LOCATION_SENT) {
                convertView = inflater.inflate(R.layout.item_chat_location_sent, parent, false);
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
            Logger.e("语音路径：" + message.getFileName());
            // 语音的点击事件
            holder.layout_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (message.getMessageState() == MyMessage.MessageState.Sucess.getType()) {
                        final File file = new File(message.getFileName());
                        Logger.e("exists = " + file.exists());
                        if (file.exists()) {
                            try {
                                RecordManager.playAudio(context, message.getFileName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        ToastUtils.showLongToast("语音文件异常");
                    }
                }
            });
        } else if (message.getMessageType() == MyMessage.MessageType.Image.getType()) { // 图片
            Logger.e("图片地址：" + message.getFileName());
            Bitmap bitmap = BitmapUtils.createImage(message.getFileName());
            holder.iv_message_img.setImageBitmap(bitmap);
            holder.iv_message_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> imgList = new ArrayList<>();
                    imgList.add(message.getFileName());
                    final Intent intent = new Intent(context, ZoomPictureActivity.class);
                    intent.putExtra("position", 0);
                    intent.putExtra("imgList", imgList);
                    context.startActivity(intent);
                }
            });
        } else if (message.getMessageType() == MyMessage.MessageType.File.getType()) { // 文件
            Logger.e("文件地址：" + message.getFileName());
            holder.tv_chat_item_fileName.setText(message.getFileName());
            holder.tv_chat_item_fileSize.setText(message.getFileSize());
            holder.rl_chat_item_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showLongToast("打开文件");
                }
            });
        } else if (message.getMessageType() == MyMessage.MessageType.Location.getType()) { // 地理位置
            holder.tv_chat_item_location.setText(message.getAddress());
            holder.tv_chat_item_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(context, JSBaiDuMapActivity.class);
                    if (!Utils.isNull(message.getLatitude()) && !Utils.isNull(message.getLongitude()))
                        intent.putExtra("latitude", Double.valueOf(message.getLatitude()));
                    intent.putExtra("longitude", Double.valueOf(message.getLongitude()));
                    intent.putExtra("address", message.getAddress());
                    intent.putExtra("send", false);
                    context.startActivity(intent);
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
        // 图片-发送消息的图片
        ImageView iv_message_img;
        // file消息的父容器：用来做点击事件
        RelativeLayout rl_chat_item_file;
        // 文件名称
        TextView tv_chat_item_fileName;
        // 文件大小
        TextView tv_chat_item_fileSize;
        // 地理位置描述
        TextView tv_chat_item_location;


        public ViewHolder(View convertView) {
            if (convertView == null) return;
            view_read_status = convertView.findViewById(R.id.view_read_status);

            iv_head_view = (CircleImageView) convertView.findViewById(R.id.iv_head_view);

            tv_chat_time = (TextView) convertView.findViewById(R.id.tv_chat_time);
            tv_message_text = (TextView) convertView.findViewById(R.id.tv_message_text);
            tv_voice_duration = (TextView) convertView.findViewById(R.id.tv_voice_duration);
            tv_chat_item_fileName = (TextView) convertView.findViewById(R.id.tv_chat_item_fileName);
            tv_chat_item_fileSize = (TextView) convertView.findViewById(R.id.tv_chat_item_fileSize);
            tv_chat_item_location = (TextView) convertView.findViewById(R.id.tv_chat_item_location);

            iv_voice_error = (ImageView) convertView.findViewById(R.id.iv_voice_error);
            iv_message_img = (ImageView) convertView.findViewById(R.id.iv_message_img);

            layout_voice = (LinearLayout) convertView.findViewById(R.id.layout_voice);

            rl_chat_item_file = (RelativeLayout) convertView.findViewById(R.id.rl_chat_item_file);
        }
    }
}
