package com.jj.investigation.openfire.view.chatbottom;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.impl.ChatPictureSelectedListener;
import com.jj.investigation.openfire.impl.ChatTextSendListener;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.utils.Utils;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumListener;

import org.jivesoftware.smack.chat.Chat;

import java.util.ArrayList;

/**
 * 聊天界面下方的输入框：
 * 包括输入框下面的表情menu、选择图片位置等的menu
 * Created by ${R.js} on 2018/1/19.
 */

public class JSChatBottomView extends LinearLayout {

    private Context context;
    // 包裹emoj和选择图片文件menu的容器
    private FrameLayout fl_menu_container;
    // 选择图片和文件等的menu
    private JSChatPlusMenu jschat_plus_menu;
    // 选择emoj的menu
    private JSChatEmojMenu jschat_emoj_menu;
    private JSChatInputView jschat_key_view;
    private Chat chat;
    // 选择图片完成的监听
    private ChatPictureSelectedListener chatSelectPictureListener;
    // 发送文本消息的监听
    private ChatTextSendListener chatTextSendListener;
    private EditText et_message;
    public static final int FILE_SELECT_CODE = 100;

    public JSChatBottomView(Context context) {
        this(context, null);
    }

    public JSChatBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        initClickListener();
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.js_chat_bottom, this);

        fl_menu_container = (FrameLayout) findViewById(R.id.fl_menu_container);
        jschat_key_view = (JSChatInputView) findViewById(R.id.jschat_key_view);
        jschat_plus_menu = (JSChatPlusMenu) findViewById(R.id.jschat_plus_menu);
        jschat_emoj_menu = (JSChatEmojMenu) findViewById(R.id.jschat_emoj_menu);

        et_message = jschat_key_view.getEt_message();
    }

    /**
     * 设置点击事件的回调
     * 之所以要回调，是因为要在Activity中发送消息，发送消息也可以在各自的View中发送，但是还要
     * 为每个View设置获取jid、对方jid、chat等各种参数，也比较麻烦，现在只是做demo，先要做出效果，
     * 做好后可以统一做优化
     */
    private void initClickListener() {

        // 1.弹出或隐藏更多menu的点击事件
        jschat_key_view.setOnPlusMenuClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fl_menu_container.getVisibility() == View.GONE) {
                    Utils.hintKbTwo(context, jschat_key_view);
                    fl_menu_container.setVisibility(View.VISIBLE);
                    jschat_plus_menu.setVisibility(View.VISIBLE);
                } else {
                    if (jschat_emoj_menu.getVisibility() == View.VISIBLE) {
                        jschat_emoj_menu.setVisibility(View.GONE);
                        jschat_plus_menu.setVisibility(View.VISIBLE);
                    } else {
                        fl_menu_container.setVisibility(View.GONE);
                    }
                }
            }
        });

        // 2.弹出或隐藏emojmenu的点击事件
        jschat_key_view.setOnEmojMenuClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fl_menu_container.getVisibility() == View.GONE) {
                    Utils.hintKbTwo(context, jschat_key_view);
                    fl_menu_container.setVisibility(View.VISIBLE);
                    jschat_emoj_menu.setVisibility(View.VISIBLE);
                } else {
                    if (jschat_plus_menu.getVisibility() == View.VISIBLE) {
                        jschat_plus_menu.setVisibility(View.GONE);
                        jschat_emoj_menu.setVisibility(View.VISIBLE);
                    } else {
                        fl_menu_container.setVisibility(View.GONE);
                    }
                }
            }
        });

        // 3.输入框的点击事件：如果menu的容器显示，则点击输入框后让其不显示
        jschat_key_view.setOnKeyboardClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fl_menu_container.getVisibility() == View.VISIBLE) {
                    fl_menu_container.setVisibility(View.GONE);
                }
            }
        });

        // 4.更多menu中item的点击事件
        jschat_plus_menu.setPlusMenuItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==0) {
                    selectFile();
                } else if (position == 1) {
                    selectPicture();
                }
            }
        });

        // 5.发送消息的点击事件回调
        jschat_key_view.setOnTxtSendListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatTextSendListener != null) {
                    final String content = et_message.getText().toString().trim();
                    if (!Utils.isNull(content)) {
                        chatTextSendListener.textSend(content);
                        et_message.setText("");
                    }
                }
            }
        });
    }

    /**
     * 设置聊天会话
     * @param chat 聊天会话
     */
    public void setChatManager(Chat chat) {
        this.chat = chat;
        jschat_key_view.setChat(chat);
    }

    /**
     * 设置选择图片完成的监听:
     * 选择普通文件不用监听，因为打开系统文件，选择好之后可以直接在Activity的
     * onActivityResult中得到文件，所以省去了来回回调的步骤
     */
    public void setChatPictureSelectedListener(ChatPictureSelectedListener chatPictureSelectedListener) {
        this.chatSelectPictureListener = chatPictureSelectedListener;
    }

    /**
     * 发送文本消息的监听
     */
    public void setChatTextSendListener(ChatTextSendListener chatTextSendListener) {
        this.chatTextSendListener = chatTextSendListener;
    }


    /**
     * 选择图片
     */
    private void selectPicture() {
        Album.image(context)
                .multipleChoice()
                .requestCode(200)
                .camera(true)
                .columnCount(2)
                .selectCount(1)
                .listener(new AlbumListener<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAlbumResult(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        if (chatSelectPictureListener != null) {
                            chatSelectPictureListener.pictureSelected(result);
                        }
                    }

                    @Override
                    public void onAlbumCancel(int requestCode) {
                    }
                })
                .start();
    }

    /**
     * 选择文件
     */
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            ((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, "请选择文件"),
                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            ToastUtils.showShortToastSafe("此手机不支持!");
        }
    }
}
