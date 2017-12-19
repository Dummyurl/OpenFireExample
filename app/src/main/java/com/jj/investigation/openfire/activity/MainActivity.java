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
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.ContactsListAdapter;
import com.jj.investigation.openfire.smack.RosterManager;
import com.jj.investigation.openfire.smack.XmppManager;

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

/**
 * 首页
 * Created by ${R.js} on 2017/12/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ExpandableListView.OnChildClickListener {

    private ExpandableListView elv_friend;
    private ContactsListAdapter adapter;

    // 对方添加我为好友的请求
    private static final int SUBSCRIB = 1;
    // 对方同意添加我为好友
    private static final int SUBSCRIBED = 2;
    // 更新好友状态
    private static final int UPDATE_STATUS = 3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUBSCRIB:
                    showSubscribeDialog((String) msg.obj);
                    break;
                case SUBSCRIBED:
                    showSubscribedDialog((String) msg.obj);
                    break;
                case UPDATE_STATUS:
                    System.out.println("刷新--handler");
                    adapter.notifyDataSetChanged();
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
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("搜索好友");
        TextView tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("添加");
        tv_right.setOnClickListener(this);
        tv_title.setOnClickListener(this);

        elv_friend = (ExpandableListView) findViewById(R.id.elv_friend);
    }


    private void initData() {
        adapter = new ContactsListAdapter(this);
        elv_friend.setAdapter(adapter);
        new GetContactsTask().execute();
        elv_friend.setOnChildClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                initData();
                break;
            case R.id.tv_right:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }

    }

    // 联系人点击事件
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("jid", adapter.getChild(groupPosition, childPosition).getJid());
        startActivity(intent);
        return true;
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
            adapter.setGroups(roster, groups);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 监听：
     * 对方同意添加好友，对方发起好友的申请、好友上线、下线等
     * 因为监听的方法是在子线程中执行的，所以更新UI的方法通过handler在主线程中更新
     */
    private void addFriendListener() {
        XMPPTCPConnection connection = XmppManager.getConnection();
        connection.addAsyncStanzaListener(new StanzaListener() { // 处理消息
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                // 只对好友申请状态消息处理
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType().name().equals(Presence.Type.subscribe.name())) { // 对方添加我为好友
                        if (RosterManager.get().isAdd(presence.getFrom())) {
                            Message message = handler.obtainMessage(SUBSCRIB, presence.getFrom());
                            handler.sendMessage(message);
                        }
                    } else if (presence.getType().name().equals(Presence.Type.subscribed.name())) { // 对方同意加我为好友
                        // 对方同意了我的请求，并且回执这个请求，所以需要处理（刷新好友列表）
                        Message message = handler.obtainMessage(
                                SUBSCRIBED, presence.getFrom());
                        handler.sendMessage(message);
                    } else if (presence.getType().name().equals(Presence.Type.available.name())) { // 好友上线
                        // 如果是好友上线或者下线，则先在adapter中更新数据源，然后通过handler在UI线程更新UI
                        adapter.upDataContactStatus(presence);
                        handler.sendEmptyMessage(UPDATE_STATUS);
                    } else if (presence.getType().name().equals(Presence.Type.unavailable.name())) { // 好友下线
                        adapter.upDataContactStatus(presence);
                        handler.sendEmptyMessage(UPDATE_STATUS);
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
                        // 互为好友
                        RosterManager.get().addFriend(nickName, "Friends", null);
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

    /**
     * 别人同意添加我为好友的通知，添加完毕后，刷新我的好友列表
     *
     * @param jid 对方的唯一标识
     */
    private void showSubscribedDialog(String jid) {
        // 刷新数据
        initData();
        String nickName = XmppStringUtils.parseLocalpart(jid);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示").setMessage("恭喜你，" + nickName + "已添加你为好友!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出登录
        XmppManager.getConnection().instantShutdown();
    }
}
