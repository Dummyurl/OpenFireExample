package com.jj.investigation.openfire.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.XmppManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_search);
        et_search = (EditText) findViewById(R.id.et_search);
    }

    // 搜索好友
    public void search(View v) {
        String keyword = et_search.getText().toString();
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

        private ArrayAdapter<String> arrayAdapter;

        @Override
        protected Boolean doInBackground(String... params) {
            // 搜索？
            XMPPTCPConnection connection = XmppManager.getConnection();

            try {
                ReportedData data = null;

                // 创建搜索管理器
                UserSearchManager usersearchManager = new UserSearchManager(
                        connection);
                // 得到一个搜索表单
                // 指定搜索服务器名称(标记当前执行的搜索服务)
                // 前缀：代表你的搜索服务类型
                String searchService = "search." + XmppManager.SERVICE_NAME;
                Form f = usersearchManager.getSearchForm(searchService);

                // 设置条件表单
                Form answer = f.createAnswerForm();
                answer.setAnswer("Username", true);
                answer.setAnswer("search", params[0]);

                // 发起搜索，获取搜索结果
                data = usersearchManager
                        .getSearchResults(answer, searchService);

                // 解析搜索结果
                ArrayList<String> userList = new ArrayList<String>();
                for (ReportedData.Row row : data.getRows()) {
                    // 有两个值:分别是jid和username
                    String username = row.getValues("Username").get(0);
                    // 后面我们需要根据jid添加好友
                    userList.add(username);
                }
                arrayAdapter = new ArrayAdapter<String>(SearchActivity.this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, userList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // ui线程更新
            if (arrayAdapter != null) {
                listView.setAdapter(arrayAdapter);
            }
        }
    }

}
