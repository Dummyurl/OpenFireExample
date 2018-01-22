package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.JSChatPlusMenuAdapter;

/**
 * 聊天界面--点击加号选择图片、位置等的menu
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatPlusMenu extends LinearLayout {

    private Context context;
    private GridView gv_plus_menu;


    public JSChatPlusMenu(Context context) {
        this(context, null);
    }

    public JSChatPlusMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAdapter();
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.js_chat_plus_menu, this);
        gv_plus_menu = (GridView) findViewById(R.id.gv_plus_menu);

    }


    private void initAdapter() {
        JSChatPlusMenuAdapter adapter = new JSChatPlusMenuAdapter(context);
        gv_plus_menu.setAdapter(adapter);
    }

    /**
     * 条目的点击事件
     */
    public void setPlusMenuItemClickListener(AdapterView.OnItemClickListener listener) {
        gv_plus_menu.setOnItemClickListener(listener);
    }

}
