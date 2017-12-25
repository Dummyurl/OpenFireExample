package com.jj.investigation.openfire.bean;

import java.util.List;

/**
 * 联系人列表-分组
 * Created by ${R.js} on 2017/12/15.
 */

public class ContactGroup {

    // 分组名称
    private String groupName;
    // 分组描述
    private String groupDesc;
    // 该分组下的好友列表
    private List<User> contactChildList;
    // 该群组的成员数两
    private int count;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public List<User> getContactChildList() {
        return contactChildList;
    }

    public void setContactChildList(List<User> contactChildList) {
        this.contactChildList = contactChildList;
    }

    @Override
    public String toString() {
        return "ContactGroup{" +
                "groupName='" + groupName + '\'' +
                ", groupDesc='" + groupDesc + '\'' +
                ", contactChildList=" + contactChildList +
                ", count=" + count +
                '}';
    }
}
