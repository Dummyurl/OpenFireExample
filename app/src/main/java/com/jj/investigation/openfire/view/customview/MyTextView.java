package com.jj.investigation.openfire.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.utils.Utils;

/**
 * 自定义TextView
 * Created by ${R.js} on 2018/2/1.
 */

public class MyTextView extends View {

    private Context context;
    private String text;
    private int textSize = 15;
    private int color = Color.BLACK;
    private Paint paint;
    private Rect textRec;


    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        // 设置文字大小与颜色
        paint.setTextSize(textSize);
        paint.setColor(color);
    }

    private void setAttrs(AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyTextView);
        text = array.getString(R.styleable.MyTextView_js_text);
        color = array.getColor(R.styleable.MyTextView_js_text_color, color);
        textSize = array.getDimensionPixelSize(R.styleable.MyTextView_js_text_size, Utils.sp2px(context, textSize));
        array.recycle();
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
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        // 普通的View就只用到两种模式，一个是at_most 一个是exactly
        // 如果是exactly则不用管，如果是at_most则需要处理

        int width = getPaddingLeft() + getPaddingRight();
        int height = getPaddingTop() + getPaddingBottom();
        textRec = new Rect();

        if (widthMode == MeasureSpec.EXACTLY) {
            width = width + MeasureSpec.getSize(widthMeasureSpec);
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            // 用画笔测量文字的宽高，paint.measureText(text)可以直接获取文字的宽度，但是并没有直接测量获取
            // 高度的方法，所以用getBounds的方法获取文字的宽度和高度
            paint.getTextBounds(text, 0, text.length(), textRec);
            width = width + textRec.width();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = height + MeasureSpec.getSize(heightMeasureSpec);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            paint.getTextBounds(text, 0, text.length(), textRec);
            height = height + textRec.height();
        }

        // 设置控件的宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 基线的确定
        final int dy = (textRec.bottom - textRec.top) / 2 - textRec.bottom;
        final int baseLine = getHeight() / 2 + dy;

        canvas.drawText(text, getPaddingLeft(), baseLine, paint);
    }
}
