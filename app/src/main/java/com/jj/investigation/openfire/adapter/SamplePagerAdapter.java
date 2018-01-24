package com.jj.investigation.openfire.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.Utils;
import com.jj.investigation.openfire.zoom.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by ${R.js} on 2017/5/26.
 */

public class SamplePagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> list;

    public SamplePagerAdapter(List<String> list, Context context) {
        this.mContext = context;
        this.list = list;
        Logger.e("文件size = " + list.size());
    }

    @Override
    public int getCount() {
        Logger.e("文件size2 = " + list.size());
        return list.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        Logger.e("instantiateItem");
        final PhotoView photoView = new PhotoView(container.getContext());
        if (!Utils.isNull(list.get(position))) {
            final File file = new File(list.get(position));
            Logger.e("文件是否存在：" + file.exists());
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(list.get(position));
                photoView.setImageBitmap(bitmap);
            } else {
                Picasso.with(mContext).load(list.get(position)).into(photoView);
            }
        }

        // 添加PhotoView到ViewPager并返回
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
