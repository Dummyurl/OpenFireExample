package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;

/**
 * 聊天界面下方的输入框：
 *      包括输入框下面的表情menu、选择图片位置等的menu
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatBottomView extends LinearLayout {

    private Context context;

    public JSChatBottomView(Context context) {
        this(context, null);
    }

    public JSChatBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.js_chat_bottom, this);
    }
}
