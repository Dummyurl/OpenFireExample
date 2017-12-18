package com.jj.investigation.openfire.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.XmppManager;
import com.jj.investigation.openfire.adapter.ContactsListAdapter;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView elv_friend;
    private ContactsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
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

}
