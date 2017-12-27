package com.jj.investigation.openfire.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jj.investigation.openfire.R;


/**
 * 录音的Toast工具类
 */
public class ToastRecord {
    private static Toast toast;

    private static ImageView imageView;

    public static void showToast(Context context, int imageRes) {
        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            View view = LayoutInflater.from(context).inflate(
                    R.layout.chat_toast_record_layout, null);
            imageView = (ImageView) view.findViewById(R.id.iv_record);
            toast.setView(view);
            //设置Toast在屏幕中显示位置(屏幕居中显示)
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        imageView.setImageDrawable(context.getResources().getDrawable(imageRes));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void hideToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
