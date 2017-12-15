package com.jj.investigation.openfire;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * XMPP的参数设置
 * Created by ${R.js} on 2017/12/15.
 */
public class XmppManager {

    private static XMPPTCPConnection xmppConnection;

    // 域名：IP地址
    private static final String HOST = "192.168.1.156";
    // 端口号(固定，就是5222)
    private static final int PORT = 5222;
    // 服务器名称：openfire管理页面中的服务器名称
    private static final String SERVICE_NAME = "pc201501230929";

    /**
     * 获取连接对象
     *
     * @return
     */
    public static XMPPTCPConnection getConnection() {
        if (xmppConnection == null) {
            xmppConnection = openConnection();
        }
        return xmppConnection;
    }

    /**
     * 打开连接
     *
     */
    private static XMPPTCPConnection openConnection() {
        if (!isConnected()) {
            XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
                    .builder()
                    .setHost(HOST)
                    .setPort(PORT)
                    .setServiceName(SERVICE_NAME)
                    .setConnectTimeout(Integer.MAX_VALUE)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            xmppConnection = new XMPPTCPConnection(builder.build());
            try {
                xmppConnection.connect();
            } catch (Exception e) {
                System.out.println("错误原因XMPPTCPConnection = " + e.toString());
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

}
