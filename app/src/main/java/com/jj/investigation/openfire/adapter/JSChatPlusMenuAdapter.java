package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;

/**
 * 聊天页面--下方输入框--点击+号弹出来的menu菜单的adapter
 * Created by ${R.js} on 2018/1/22.
 */

public class JSChatPlusMenuAdapter extends BaseAdapter {

    private Context context;
    private String[] desc = {
            "文件", "图片", "位置"
    };
    private int[] imgs = {
            R.drawable.js_chat_file_selector,
            R.drawable.js_chat_image_selector,
            R.drawable.js_chat_location_selector
    };

    public JSChatPlusMenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return desc.length;
    }

    @Override
    public Object getItem(int position) {
        return desc[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.item_jschat_plus_menu, null);
        ImageView iv_jschat_plus_menu = (ImageView) convertView.findViewById(R.id.iv_jschat_plus_menu);
        TextView tv_jschat_plus_menu = (TextView) convertView.findViewById(R.id.tv_jschat_plus_menu);
        iv_jschat_plus_menu.setImageResource(imgs[position]);
        tv_jschat_plus_menu.setText(desc[position]);
        return convertView;
    }
}
