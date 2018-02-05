package com.jj.investigation.openfire.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义TextView
 * Created by ${R.js} on 2018/2/1.
 */

public class MyTextView extends View {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 布局的宽高都需要由该方法指定
        // 指定控件的宽高，需要测量
        // 获取宽高的模式:就是布局中宽高写的match_parent,wrap_content,100dp等
        // widthMode == MeasureSpec.AT_MOST：在布局中使用了wrap_content
        // widthMode == MeasureSpec.EXACTLY：在布局中使用了确切的值，例如100dp，match_parent
        // widthMode == MeasureSpec.UNSPECIFIED：未指定值，但是尽量的大，满足控件的大小。
        //        一般为ListView、ScrollView这样不确定大小的控件，在测量子布局的时候会使用到UNSPECIFIED
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED) {

        }

    }
}
