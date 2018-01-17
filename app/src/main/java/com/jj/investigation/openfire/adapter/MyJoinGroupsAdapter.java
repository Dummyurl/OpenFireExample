package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.IMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 我加入的群组
 * Created by ${R.js} on 2018/1/16.
 */

public class MyJoinGroupsAdapter extends BaseAdapter {

    private List<IMGroup> groupsList;
    private LayoutInflater inflater;

    public MyJoinGroupsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        groupsList = new ArrayList<>();
    }

    /**
     * 加载数据
     */
    public void setGroups(List<IMGroup> groups) {
        groupsList.clear();
        groupsList.addAll(groups);
    }

    @Override
    public int getCount() {
        return groupsList == null ? 0 : groupsList.size();
    }

    @Override
    public IMGroup getItem(int position) {
        if (groupsList != null && groupsList.get(position) != null) {
            return groupsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_my_join_group, parent,
                false);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        tv_name.setText(groupsList.get(position).getGroupname());
        return convertView;
    }
}
