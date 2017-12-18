package com.jj.investigation.openfire.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.smack.RosterManager;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.adapter.ContactsListAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.util.XmppStringUtils;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView elv_friend;
    private ContactsListAdapter adapter;

    // 对方添加我为好友的请求
    private static final int SUBSCRIB = 1;
    // 对方同意添加我为好友
    private static final int SUBSCRIBED = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("接收到发送的消息", msg.what + ",  = " + msg.obj.toString());
            switch (msg.what) {
                case SUBSCRIB:
                    // 接受好友请求
                    showSubscribeDialog((String) msg.obj);
                    break;
                case SUBSCRIBED:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        addFriendListener();
    }

    private void initView() {
        elv_friend = (ExpandableListView) findViewById(R.id.elv_friend);
    }


    private void initData() {
        adapter = new ContactsListAdapter(this);
        elv_friend.setAdapter(adapter);
        new GetContactsTask().execute();
    }


    /**
     * 获取联系人列表
     */
    class GetContactsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            XMPPTCPConnection connection = XmppManager.getConnection();
            Roster roster = Roster.getInstanceFor(connection);
            Collection<RosterGroup> groups = roster.getGroups();
            adapter.setGroups(groups);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            adapter.notifyDataSetChanged();
        }
    }

    public void search(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    /**
     * 接收好友请求的监听
     */
    private void addFriendListener() {
        XMPPTCPConnection connection = XmppManager.getConnection();
        connection.addAsyncStanzaListener(new StanzaListener() { // 处理消息
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                Log.e("好友申请：", "packet = " + packet.toString());
                // 只对好友申请状态消息处理
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType().name().equals(Presence.Type.subscribe.name())) {
                        if (RosterManager.get().isAdd(presence.getFrom())) {
                            Message message = handler.obtainMessage(SUBSCRIB, presence.getFrom());
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }, new StanzaFilter() { // 消息过滤器，默认所有消息都做处理
            @Override
            public boolean accept(Stanza stanza) {
                // 返回true，则处理所有消息，如果返回false，则过滤掉的消息是不会再上面方法中做处理的
                return true;
            }
        });
    }


    /**
     * 显示对方添加我为好友的dialog
     */
    private void showSubscribeDialog(final String jid) {
        final String nickName = XmppStringUtils.parseLocalpart(jid);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示").setMessage(nickName + "向你发送了好友请求，是否添加为好友？")
                .setPositiveButton("接受", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 同意
                        RosterManager.get().accept(jid);
                        // 刷新列表
                        initData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RosterManager.get().refuse(jid);
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
