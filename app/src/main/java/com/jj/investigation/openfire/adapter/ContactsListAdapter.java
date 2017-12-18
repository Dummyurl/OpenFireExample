package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.ContactChild;
import com.jj.investigation.openfire.bean.ContactGroup;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 联系人列表
 * Created by ${R.js} on 2017/12/15.
 */

public class ContactsListAdapter extends BaseExpandableListAdapter {

    // 分组列表
    private List<ContactGroup> groupList;
    // 每个分组下面的联系人列表
    private List<List<ContactChild>> childList;
    private LayoutInflater layoutInflater;


    public ContactsListAdapter(Context context) {
        this.groupList = new ArrayList<>();
        this.childList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList == null ? 0 : childList.get(groupPosition).size();
    }

    @Override
    public ContactGroup getGroup(int groupPosition) {
        return groupList == null ? null : groupList.get(groupPosition);
    }

    @Override
    public ContactChild getChild(int groupPosition, int childPosition) {
        return childList == null ? null : childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_contact_group, parent, false);
        TextView tv_friend_group = (TextView) convertView.findViewById(R.id.tv_friend_group);
        ContactGroup group = getGroup(groupPosition);
        tv_friend_group.setText(group.getGroupName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_contact_child, parent, false);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        ContactChild child = getChild(groupPosition, childPosition);
        tv_name.setText(child.getUserName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 设置联系人数据
     */
    public void setGroups(Collection<RosterGroup> groups) {
        if (groups != null && groups.size() != 0) {
            ContactGroup group;
            ContactChild child;
            // 自己定义的好友列表
            List<ContactChild> contactChilList;
            // Smack的好友数据列表
            List<RosterEntry> entryList;
            for (RosterGroup rosterGroup : groups) {
                group = new ContactGroup();
                group.setGroupName(rosterGroup.getName());
                groupList.add(group);

                contactChilList = new ArrayList<>();
                entryList = rosterGroup.getEntries();
                for (RosterEntry entry : entryList) {
                    child = new ContactChild(entry.getName(), "", "");
                    contactChilList.add(child);
                }
                childList.add(contactChilList);
            }
        }
    }
}
