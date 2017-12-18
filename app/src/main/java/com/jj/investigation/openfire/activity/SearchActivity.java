package com.jj.investigation.openfire.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


        private ArrayAdapter<String> adapter;

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
                adapter = new ArrayAdapter<>(SearchActivity.this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, userList);
                if (userList.size() > 0) {
                    searchResult = true;
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
