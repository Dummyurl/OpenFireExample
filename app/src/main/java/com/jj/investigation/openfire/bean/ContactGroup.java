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
    private List<ContactChild> contactChildList;


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

    public List<ContactChild> getContactChildList() {
        return contactChildList;
    }

    public void setContactChildList(List<ContactChild> contactChildList) {
        this.contactChildList = contactChildList;
    }
}
