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
        return messageList.get(position).getOprationType();
    }

    @Override
    public int getViewTypeCount() {
        return MyMessage.OprationType.values().length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MyMessage message = getItem(position);
        if (convertView == null) {
            if (message.getOprationType() == MyMessage.OprationType.Send.getType()) {
                convertView = inflater.inflate(R.layout.item_chat_text_sent,
                        parent, false);
            } else if (message.getOprationType() == MyMessage.OprationType.Receiver.getType()) {
                convertView = inflater.inflate(R.layout.item_chat_text_rece,
                        parent, false);
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (message == null) {
            message = new MyMessage("", "", "", "", 0);
        }
        holder.tv_chat_time.setText(message.getData());
        holder.tv_message_text.setText(message.getContent());
        return convertView;
    }


    class ViewHolder {
        TextView tv_chat_time;
        CircleImageView iv_head_view;
        TextView tv_message_text;

        public ViewHolder(View convertView) {
            tv_chat_time = (TextView) convertView
                    .findViewById(R.id.tv_chat_time);
            iv_head_view = (CircleImageView) convertView
                    .findViewById(R.id.iv_head_view);
            tv_message_text = (TextView) convertView
                    .findViewById(R.id.tv_message_text);
        }
    }
}
