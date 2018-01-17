package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.IMGroup;

import java.util.List;

/**
 * 我加入的群组
 * Created by ${R.js} on 2018/1/16.
 */

public class MyJoinGroupsAdapter extends BaseAdapter {

    private List<IMGroup> groups;
    private LayoutInflater inflater;

    public MyJoinGroupsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    /**
     * 加载数据
     */
    public void setGroups(List<IMGroup> groups) {
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups == null ? 0 : groups.size();
    }

    @Override
    public IMGroup getItem(int position) {
        if (groups != null && groups.get(position) != null) {
            return groups.get(position);
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
        tv_name.setText(groups.get(position).getGroupname());
        return convertView;
    }
}
