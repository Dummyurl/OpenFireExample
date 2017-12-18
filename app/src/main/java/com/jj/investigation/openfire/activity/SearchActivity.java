package com.jj.investigation.openfire.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.SearchContactAdapter;
import com.jj.investigation.openfire.impl.AddFriendListener;
import com.jj.investigation.openfire.smack.RosterManager;
import com.jj.investigation.openfire.smack.XmppManager;
import com.jj.investigation.openfire.utils.ToastUtils;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;

/**
 * 联系人列表
 *
 * @author Dream
 */
public class SearchActivity extends Activity {
    private ListView listView;
    private EditText et_search;
    private SearchContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("搜索好友");
        listView = (ListView) findViewById(R.id.lv_search);
        et_search = (EditText) findViewById(R.id.et_search);
    }

    private void initData() {
        adapter = new SearchContactAdapter(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAddFriendDialog(position);
            }
        });
    }

    private AddFriendListener listener = new AddFriendListener() {
        @Override
        public void sendSuccess() {
            ToastUtils.showShortToastSafe("请求已发送");
        }

        @Override
        public void sendFailed() {
            ToastUtils.showLongToast("已是好友或正在等待对方处理");
        }
    };

    /**
     * 显示添加好友的对话框
     * @param position 联系人在列表中的位置
     */
    private void showAddFriendDialog(final int position) {
        final String nickName = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加好友").setMessage("是否添加[" + nickName + "]为好友？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 发起添加好友的一方
                        RosterManager.get().addFriend(nickName, "Friends", listener);
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

    // 搜索好友
    public void search(View v) {
        String keyword =  et_search.getText().toString();
        if (keyword.isEmpty()) {
            ToastUtils.showShortToastSafe("不能为空");
            return;
        }
        SearchTask searchTask = new SearchTask();
        searchTask.execute(keyword);
    }

    /**
     * 搜索任务
     *
     * @author Dream
     */
    class SearchTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean searchResult = false;

            try {
                // 获取连接对象
                XMPPTCPConnection connection = XmppManager.getConnection();
                // 声明接收数据的类型
                ReportedData data;
                // 创建搜索管理器
                UserSearchManager userSearchManager = new UserSearchManager(connection);
                // 指定搜索服务器的名称
                String serviceName = "search." + XmppManager.SERVICE_NAME;
                // 创建搜索表单
                Form searchForm = userSearchManager.getSearchForm(serviceName);
                // 设置返回数据的条件表单
                Form answerForm = searchForm.createAnswerForm();
                answerForm.setAnswer("Username", true);
                answerForm.setAnswer("search", params[0]);
                // 发起搜索并获取搜索结果
                data = userSearchManager.getSearchResults(answerForm, serviceName);
                if (data == null) {
                    return searchResult;
                }
                // 解析数据结果
                ArrayList<String> userList = new ArrayList<>();
                for (ReportedData.Row row:data.getRows()) {
                    String username = row.getValues("Username").get(0);
                    userList.add(username);
                }

                if (userList.size() > 0) {
                    searchResult = true;
                    adapter.addData(userList);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("搜索联系人错误信息：" + getClass().getName(), e.toString());
            }

            return searchResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // ui线程更新
            if (result && adapter != null) {
                listView.setAdapter(adapter);
            } else {
                ToastUtils.showShortToastSafe("无相关联系人");
            }
        }
    }

}
