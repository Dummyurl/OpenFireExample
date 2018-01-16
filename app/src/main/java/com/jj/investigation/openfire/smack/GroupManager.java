package com.jj.investigation.openfire.smack;

import com.jj.investigation.openfire.utils.Logger;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 群组管理类:
 * 1）创建群组
 * 2）加入群组
 * 3）获取群组列表
 * Created by ${R.js} on 2018/1/16.
 */

public class GroupManager {

    private static final String CONFERENCE = "@conference.";

    /**
     * 创建一个群组
     *
     * @param groupName 群组名称
     * @param groupPass 群组密码
     * @param desc      群组简介
     */
    public static MultiUserChat groupCreate(XMPPTCPConnection connection, String groupName,
                                            String groupPass, String desc) throws Exception {

        // 群聊管理器--创建群组
        final MultiUserChatManager chatManager = MultiUserChatManager.getInstanceFor(connection);
        // 参数是群的ID，格式是：房间名@conference.服务器名称
        final String jid = groupName + CONFERENCE + connection.getServiceName();
        Logger.e("群组jid：" + jid);
        // 创建房间管理器
        final MultiUserChat multiUserChat = chatManager.getMultiUserChat(jid);
        // 创建群组，如果群组不存在，则创建，如果存在则加入
        multiUserChat.createOrJoin(groupName);
        // 对群组进行配置
        // 获取默认的配置
        final Form defauleForm = multiUserChat.getConfigurationForm();
        final List<FormField> defaultFields = defauleForm.getFields();
        // 如果需要设置自定义配置，则创建新的表单
        final Form myForm = defauleForm.createAnswerForm();
        // 默认配置还需要再次设置
        for (FormField field : defaultFields) {
            // 如果存在，则配置默认信息
            if (!field.getType().equals(FormField.Type.hidden) && field.getVariable() != null) {
                myForm.setDefaultAnswer(field.getVariable());
            }
        }

        // 下面为设置其他配置信息：
        // 一些额外的配置
        final List<String> owners = new ArrayList<>();
        owners.add(connection.getUser());
        myForm.setAnswer("muc#roomconfig_roomowners", owners);
        myForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
        // 设置聊天室是持久聊天室，即将要被保存下来
        myForm.setAnswer("muc#roomconfig_persistentroom", true);
        // 房间仅对成员开放
        myForm.setAnswer("muc#roomconfig_membersonly", false);
        // 允许占有者邀请其他人
        myForm.setAnswer("muc#roomconfig_allowinvites", true);
        // 允许加入的成员数
        myForm.setAnswer("muc#roomconfig_maxusers", Arrays.asList("100"));
        // 能够发现占有者真实 JID 的角色
        // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
        // 登录房间对话
        myForm.setAnswer("muc#roomconfig_enablelogging", true);
        // 仅允许注册的昵称登录
        myForm.setAnswer("x-muc#roomconfig_reservednick", true);
        // 允许使用者修改昵称
        myForm.setAnswer("x-muc#roomconfig_canchangenick", true);
        // 允许用户注册房间
        myForm.setAnswer("x-muc#roomconfig_registration", false);
        // 进入是否需要密码
        myForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
        // 设置进入密码
        myForm.setAnswer("muc#roomconfig_roomsecret", groupPass);
        myForm.setAnswer("muc#roomconfig_roomdesc", desc);
        multiUserChat.sendConfigurationForm(myForm);

        return multiUserChat;
    }

    /**
     * 加入群
     * @param groupName 群名称
     * @param groupPwd 群密码
     * @param groupnickName 你在该群里要显示的昵称
     */
    public static void chatRoomJoin(XMPPTCPConnection connection, String groupName,
                                    String groupPwd, String groupnickName) throws Exception {

        // 群聊管理器--创建群组
        final MultiUserChatManager chatManager = MultiUserChatManager.getInstanceFor(connection);
        // 参数是群的ID，格式是：房间名@conference.服务器名称
        final String jid = groupName + CONFERENCE + connection.getServiceName();
        // 创建房间管理器
        final MultiUserChat multiUserChat = chatManager.getMultiUserChat(jid);
        // 历史消息记录
        final DiscussionHistory discussionHistory = new DiscussionHistory();
        discussionHistory.setSince(new Date(2018, 1, 1));
        multiUserChat.join(groupnickName, groupPwd, discussionHistory, 8 * 1000);
    }
}
