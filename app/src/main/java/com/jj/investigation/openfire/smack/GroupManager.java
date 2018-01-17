package com.jj.investigation.openfire.smack;

import com.jj.investigation.openfire.bean.IMGroup;
import com.jj.investigation.openfire.utils.Logger;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
     *
     * @param groupName     群名称
     * @param groupPwd      群密码
     * @param groupnickName 你在该群里要显示的昵称
     */
    public static void groupJoin(XMPPTCPConnection connection, String groupName,
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

    /**
     * 获取用户已经加入的群（临时会话群）
     */
    public static List<IMGroup> getJoinGroupList(XMPPTCPConnection connection) throws Exception {
        final List<IMGroup> chatRooms = new ArrayList<>();
        MultiUserChatManager manager = MultiUserChatManager
                .getInstanceFor(connection);
        // 获取已加入的房间列表
        List<HostedRoom> hostedRooms = manager.getHostedRooms(connection.getServiceName());
        Logger.e("所有群：" + hostedRooms);
        Set<String> groups = manager.getJoinedRooms();
        IMGroup group = null;
        for (String roomName : groups) {
            // 获取指定的房间信息
            RoomInfo roomInfo = manager.getRoomInfo(roomName);
            group = new IMGroup();
            // 获取jid
            group.setJid(roomInfo.getRoom());
            // 群名称
            group.setGroupname(roomInfo.getName());
            // 群描述
            group.setGorupdesc(roomInfo.getDescription());
            // 群人数
            group.setGroupnumber(roomInfo.getOccupantsCount() + "");
            chatRooms.add(group);
        }
        return chatRooms;
    }

    /**
     * 收藏一个群：在创建群和加入群后调用。
     * 以上创建群组和加入群组只是临时的，虽然创建的群组会一直存在，但是创建者在群里的成员状态却是临时的，
     * 比如我创建了一个群，则我加入的群就有该群，但是一旦退出OpenFire的登录，再进去发现我加入的群已经没有了
     * 这个群，加入群也是同样的道理，并且从OpenFire的管理页面也可以看到，创建或者加入群后，群成员有此人，
     * 但是一旦调用退出的动作，则群成员不再有该人
     */
    public static void collectGroups(XMPPTCPConnection connection, String groupName,
                                     String groupPwd, String groupnickName) throws Exception {
        // 获取收藏群管理器
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        final String jid = groupName + CONFERENCE + connection.getServiceName();
        // 收藏群组，也就是要永久加入该群，成为该群的成员
        bookmarkManager.addBookmarkedConference(groupName, jid, true, groupnickName,
                groupPwd);
    }

    /**
     * 获取我加入的群组列表（永久的）
     */
    public static List<IMGroup> getMyJoinGroupsEver(XMPPTCPConnection connection) throws Exception {
        final List<IMGroup> groupList = new ArrayList<>();
        // 获取群聊管理器
        final MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        // 获取收藏群组管理器
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection);
        // 获取收藏的群组列表
        final List<BookmarkedConference> bookmarkedConferences = bookmarkManager.getBookmarkedConferences();
        if (bookmarkedConferences == null || bookmarkedConferences.size() == 0) {
            return null;
        }
        IMGroup group = null;
        for (BookmarkedConference conference : bookmarkedConferences) {
            group = new IMGroup();
            group.setJid(conference.getJid());
            group.setGroupname(conference.getName());
            group.setGrouppassword(conference.getPassword());

            // 上面只是获取到了收藏的群组列表，还需要把当前用户加入到群里面(否则无法聊天)
            groupJoin(connection, conference.getName(), conference.getPassword(), conference.getNickname());
            RoomInfo roomInfo = multiUserChatManager.getRoomInfo(conference.getJid());
            // 获取群组的描述
            group.setGorupdesc(roomInfo.getDescription());
            // 获取群组的成员数量
            group.setGroupnumber(roomInfo.getOccupantsCount() + "");
            groupList.add(group);
        }
        return groupList;
    }
}
