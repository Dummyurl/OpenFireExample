package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.bean.ContactChild;
import com.jj.investigation.openfire.bean.ContactGroup;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
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
        final TextView tv_friend_group = (TextView) convertView.findViewById(R.id.tv_group);
        final ImageView iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
        final TextView tv_group = (TextView) convertView.findViewById(R.id.tv_group);

        final ContactGroup group = getGroup(groupPosition);


        if (isExpanded) {
            iv_arrow.setImageResource(R.drawable.icon_up);
        } else {
            iv_arrow.setImageResource(R.drawable.icon_down);
        }
        int onLionNumber = 0;

        if (group.getContactChildList() != null && group.getContactChildList().size() > 0) {
            for (ContactChild child : group.getContactChildList()) {
                if (child.isOnlion()) {
                    onLionNumber++;
                }
            }
        }

        tv_friend_group.setText(group.getGroupName());
        tv_group.setText(group.getGroupName());
        TextView tv_count = (TextView) convertView.findViewById(R.id.tv_count);
        tv_count.setText(onLionNumber + "/" + group.getCount());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_contact_child, parent, false);
        final TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        final TextView tv_online = (TextView) convertView.findViewById(R.id.tv_online);
        final ContactChild child = getChild(groupPosition, childPosition);
        tv_name.setText(child.getUserName());
        if (child.isOnlion()) {
            tv_online.setText("[在线]");
            tv_online.setSelected(true);
        } else {
            tv_online.setText("[离线]");
            tv_online.setSelected(false);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 设置联系人数据
     */
    public void setGroups(Roster roster, Collection<RosterGroup> groups) {
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
                group.setCount(rosterGroup.getEntryCount());
                groupList.add(group);

                contactChilList = new ArrayList<>();
                entryList = rosterGroup.getEntries();
                for (RosterEntry entry : entryList) {
                    child = new ContactChild(entry.getName(), "", entry.getUser());
                    // 在线状态
                    Presence presence = roster.getPresence(entry.getUser());
                    child.setOnlion(presence.isAvailable());
                    contactChilList.add(child);
                }
                childList.add(contactChilList);
                group.setContactChildList(contactChilList);
            }
        }
    }

    /**
     * 更新好友的在线状态
     */
    public void upDataContactStatus(Presence presence) {
        // 遍历联系人
        for (List<ContactChild> list : childList) {
            for (ContactChild child : list) {
                // 如果是我的好友，则设置状态
                if (presence.getFrom().contains(child.getJid())) {
                    child.setOnlion(presence.isAvailable());
                }
            }
        }
    }
}
