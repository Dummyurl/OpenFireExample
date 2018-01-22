package com.jj.investigation.openfire.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.impl.ChatPictureSelectedListener;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.view.chatbottom.JSChatBottomView;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;

/**
 * 聊天测试界面
 * Created by ${R.js} on 2018/1/19.
 */
public class ChatTestActivity extends AppCompatActivity {

    private ImageView iv_chat;
    private JSChatBottomView jschat_bottom_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test);

        iv_chat = (ImageView) findViewById(R.id.iv_chat);
        jschat_bottom_view = (JSChatBottomView) findViewById(R.id.jschat_bottom_view);

        initListener();
    }

    private void initListener() {
        jschat_bottom_view.setChatPictureSelectedListener(new ChatPictureSelectedListener() {
            @Override
            public void pictureSelected(ArrayList<AlbumFile> albumFiles) {
                Logger.e("设置图片 = " + albumFiles.toString());
                if (albumFiles != null && albumFiles.size() > 0) {
                    if (!Utils.isNull(albumFiles.get(0).getPath())) {
                        Logger.e("设置图片");
//                        Picasso.with(ChatTestActivity.this).load(albumFiles.get(0).getPath()).into(iv_chat);
                        Bitmap bitmap = BitmapFactory.decodeFile(albumFiles.get(0).getPath());
                        iv_chat.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }


}
