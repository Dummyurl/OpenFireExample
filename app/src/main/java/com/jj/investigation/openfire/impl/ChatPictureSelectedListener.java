package com.jj.investigation.openfire.impl;

import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;

/**
 * 聊天界面选择图片的监听接口
 * Created by ${R.js} on 2018/1/22.
 */

public interface ChatPictureSelectedListener {

    void pictureSelected(ArrayList<AlbumFile> albumFiles);

}
