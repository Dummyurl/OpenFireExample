package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.view.VoiceRecordButton;

/**
 * 聊天界面--输入文字、录音的布局
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatInputView extends LinearLayout implements View.OnClickListener {

    private Context context;
    private Button btn_more;
    private Button btn_send;
    private EditText et_message;
    private VoiceRecordButton btn_voice_record;

    public JSChatInputView(Context context) {
        this(context, null);
    }

    public JSChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.js_chat_input_view, this);
        btn_voice_record = (VoiceRecordButton) findViewById(R.id.btn_voice_record);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_more = (Button) findViewById(R.id.btn_more);
        btn_send = (Button) findViewById(R.id.btn_send);
        findViewById(R.id.iv_emoj).setOnClickListener(this);
        btn_more.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                } else {
                    btn_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_emoj:
                break;
            case R.id.btn_more:
                break;
            case R.id.btn_send:
                break;

        }
    }
}
