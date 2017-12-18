package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jj.investigation.openfire.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索好友列表
 * Created by ${R.js} on 2017/12/18.
 */

public class SearchContactAdapter extends BaseAdapter {

    private List<String> nameList;
    private Context context;
    private LayoutInflater inflater;

    public SearchContactAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        nameList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    /**
     * 添加数据
     */
    public void addData(List<String> data) {
        nameList.clear();
        this.nameList = data;
    }

    @Override
    public String getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact_child, parent,
                    false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(nameList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;

        public ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_child_name);
        }
    }
}
