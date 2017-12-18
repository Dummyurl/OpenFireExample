package com.jj.investigation.openfire.smack;

import android.util.Log;

import com.jj.investigation.openfire.impl.AddFriendListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.util.XmppStringUtils;

/**
 * 添加好友管理器
 * Created by ${R.js} on 2017/12/18.
 */

public class RosterManager {
    private static RosterManager singleton = null;

    public static RosterManager get() {
        if (singleton == null) {
            singleton = new RosterManager();
        }
        return singleton;
    }

    /**
     * 接受对方添加好友的请求
     * @param jid 对方的唯一标识
     */
    public void accept(String jid) {
        // 发送接受的状态消息
        Presence presence = new Presence(Presence.Type.subscribed);
        presence.setTo(jid);
        try {
            XmppManager.getConnection().sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e("接受好友请求异常：", e.getMessage());
        }
    }

    /**
     * 拒绝对方添加好友的请求
     * @param jid
     */
    public void refuse(String jid) {
        // 发送拒绝的状态消息
        Presence presence = new Presence(Presence.Type.subscribe);
        presence.setTo(jid);
        try {
            XmppManager.getConnection().sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e("拒绝好友请求异常：", e.getMessage());
        }
    }

    /**
     * 添加好友
     *
     * @param nickName 昵称
     * @param grouName 分组名称
     */
    public void addFriend(String nickName, String grouName, AddFriendListener listener) {
        // 用户的唯一标识
        String jid = XmppStringUtils.completeJidFrom(nickName, XmppManager.SERVICE_NAME);
//        String jid = nickName + "@" + XmppManager.SERVICE_NAME; // 与上面代码结果是一样的
        Roster roster = Roster.getInstanceFor(XmppManager.getConnection());
        // 根据jid获取要添加的好友的信息，如果该值为null，则一定是没有添加好友
        RosterEntry rosterEntry = roster.getEntry(jid);
        // true:需要添加好友，false：已经是好友，不需要再添加
        boolean isSubscribed = true;

        if (rosterEntry != null) {
            // 判断当前登录用户的好友列表是否有好友
            isSubscribed = rosterEntry.getGroups().size() == 0;
        }


        // 创建添加好友信息
        if (isSubscribed && rosterEntry == null) {
            try {
                roster.createEntry(jid, nickName, new String[]{grouName});
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("请求添加好友异常：", e.toString());
            }
            listener.sendSuccess();
        } else {
            listener.sendFailed();
        }
    }

    /**
     * 判断是否添加了好友
     */
    public boolean isAdd(String jid) {
        Roster roster = Roster.getInstanceFor(XmppManager.getConnection());
        RosterEntry entry = roster.getEntry(jid);
        if (entry == null) {
            return true;
        } else {
            return false;
        }
    }
}
