package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;

/**
 * 聊天界面--点击表情选择表情的menu
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatEmojMenu extends LinearLayout {

    private Context context;
    private GridView gv_plus_menu;


    public JSChatEmojMenu(Context context) {
        this(context, null);
    }

    public JSChatEmojMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.js_chat_emoj_menu, this);

    }

}
