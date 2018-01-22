package com.jj.investigation.openfire.smack;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * XMPP的参数设置
 * Created by ${R.js} on 2017/12/15.
 */
public class XmppManager {

    private static XMPPTCPConnection xmppConnection;

    // 域名：IP地址(电脑的IPv4)
    public static final String HOST = "192.168.1.126";
    // 端口号(客户端链接服务端的端口号，这里使用5222，可以在OpenFire的管理界面中查看到多个端口号)
    public static final int PORT = 5222;
    // 服务器名称：openfire管理页面中的服务器名称
    public static final String SERVICE_NAME = "pc201501230929";

    /**
     * 获取连接对象
     */
    public static XMPPTCPConnection getConnection() {
        if (xmppConnection == null) {
            xmppConnection = openConnection();
        }
        if (!xmppConnection.isConnected()) {
            try {
                xmppConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xmppConnection;
    }

    /**
     * 打开连接
     */
    private static XMPPTCPConnection openConnection() {
        if (!isConnected()) {
            XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
                    .builder()
                    .setHost(HOST)
                    .setPort(PORT)
                    .setServiceName(SERVICE_NAME)
                    .setConnectTimeout(Integer.MAX_VALUE)
                    .setDebuggerEnabled(true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            xmppConnection = new XMPPTCPConnection(builder.build());
            try {
                xmppConnection.connect();
            } catch (Exception e) {
                System.out.println("XMPPTCPConnection链接失败:" + e.toString());
                e.printStackTrace();
            }
        }
        return xmppConnection;
    }

    /**
     * 判断是否已经连接
     */
    private static boolean isConnected() {
        return xmppConnection != null && xmppConnection.isConnected();
    }

    /**
     * 将Connection置为null--功能相当于是退出登录，所以退出登录时就调用了该方法：没有找到的官方的做法
     * 发现如果是切换用户，即使调用disconnect()方法，在登录时仍然会报错：
     * org.jivesoftware.smack.SmackException$AlreadyLoggedInException: Client is already logged in
     * 但是disconect之后并把Connection置为null，再进行切换用户则可登录成功，可能是每个用户都需要一个单独的
     * Connection，该connection会保存该用户的登录信息
     */
    public static void setConnectionNull() {
        if (xmppConnection != null) {
            if (isConnected()) {
                xmppConnection.disconnect();
            }
            xmppConnection = null;
        }
    }

}
