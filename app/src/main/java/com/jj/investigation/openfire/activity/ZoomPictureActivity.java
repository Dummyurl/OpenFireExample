package com.jj.investigation.openfire.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.adapter.SamplePagerAdapter;
import com.jj.investigation.openfire.utils.PictureSaveUtil;
import com.jj.investigation.openfire.utils.ToastUtils;
import com.jj.investigation.openfire.view.other.HackyViewPager;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * 点击图片进入该页面，可对图片进行一些处理：图片的切换、缩放、保存
 * Created by ${R.js} on 2017/5/26.
 */

public class ZoomPictureActivity extends Activity {

    // 图片URL集合
    private ArrayList<String> imgList;
    // 当前显示的图片的position
    private int currentItem;
    private HackyViewPager expanded_image;
    private ImageView iv_turn_left;
    private ImageView iv_turn_right;
    // 如果刚进入页面时，currentItem为0或最后一个，则不提示已到第一个或最后一个
    private boolean first = true;
    private ProgressBar pb_save_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_picture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initData();
    }

    private void initView() {
        iv_turn_left = (ImageView) findViewById(R.id.iv_turn_left);
        iv_turn_right = (ImageView) findViewById(R.id.iv_turn_right);
        expanded_image = (HackyViewPager) findViewById(R.id.expanded_image);
        pb_save_picture = (ProgressBar) findViewById(R.id.pb_save_picture);
    }

    private void initData() {
        int position = getIntent().getIntExtra("position", 0);
        imgList = getIntent().getStringArrayListExtra("imgList");
        if (imgList == null || imgList.size() == 0) {
            iv_turn_left.setVisibility(View.GONE);
            iv_turn_right.setVisibility(View.GONE);
        }
        if (position == 0) {
            iv_turn_left.setVisibility(View.GONE);
        }
        if (position == imgList.size() - 1) {
            iv_turn_right.setVisibility(View.GONE);
        }
        currentItem = position;
        expanded_image.setAdapter(new SamplePagerAdapter(imgList, this));
        expanded_image.setCurrentItem(currentItem);

        expanded_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!first) {
                    if (currentItem == 0 && positionOffsetPixels == 0) {
                        ToastUtils.showShortToastSafe("已是第一页");
                    }
                    if (currentItem == imgList.size() - 1 && positionOffsetPixels == 0) {
                        ToastUtils.showShortToastSafe("已是最后一页");
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                first = false;
                currentItem = position;
                if (position == 0) {
                    iv_turn_left.setVisibility(View.GONE);
                } else {
                    iv_turn_left.setVisibility(View.VISIBLE);
                }
                if (position == imgList.size() - 1) {
                    iv_turn_right.setVisibility(View.GONE);
                } else {
                    iv_turn_right.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 点击向左切换图片
     */
    public void turnLeft(View view) {
        if (currentItem != 0) {
            expanded_image.setCurrentItem(currentItem - 1);
        }
    }

    /**
     * 点击向右切换图片
     */
    public void turnRight(View view) {
        if (currentItem != imgList.size() - 1 && imgList.size() > 1) {
            expanded_image.setCurrentItem(currentItem + 1);
        }
    }

    /**
     * 保存图片的点击事件
     */
    public void savePicture(View view) {
        new PictureAsyncTask().execute(imgList.get(currentItem));
    }

    /**
     * 保存图片的AsyncTask类
     */
    private class PictureAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_save_picture.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                // 通过数组取出
                URL iconUrl = new URL(params[0]);
                URLConnection conn = iconUrl.openConnection();
                HttpURLConnection http = (HttpURLConnection) conn;

                int length = http.getContentLength();

                conn.connect();
                // 获得图像的字符流
                InputStream is = conn.getInputStream();
                // 包装输入流
                BufferedInputStream bis = new BufferedInputStream(is, length);
                // 把输入流解析成为Bitmap
                bitmap = BitmapFactory.decodeStream(bis);
                // 关流
                bis.close();
                is.close();

                // 存入到本地手机，图片名称以时间命名
                PictureSaveUtil.saveFile(bitmap, System.currentTimeMillis() + ".jpg", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pb_save_picture.setVisibility(View.GONE);
            ToastUtils.showShortToastSafe("图片保存在：/wlw/pictures目录下");
        }
    }
}
