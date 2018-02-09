package com.jj.investigation.openfire.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.Utils;

/**
 * 字体颜色随收拾滑动会产生颜色渐变的效果
 * Created by ${R.js} on 2018/2/8.
 */

public class ColorTextView extends TextView {

    private Context context;
    private int original_color = Color.BLACK;
    private int change_color = Color.RED;
    private Paint originalPaint;
    private Paint changePaint;
    private Paint thirdPaint;

    public ColorTextView(Context context) {
        this(context, null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        obtainAttrs(attrs);
        initPaint();
    }

    private void obtainAttrs(AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorTextView);
        original_color = typedArray.getColor(R.styleable.ColorTextView_orginal_color, original_color);
        change_color = typedArray.getColor(R.styleable.ColorTextView_change_color, change_color);
        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        originalPaint = getPaint(original_color);
        changePaint = getPaint(change_color);
        thirdPaint = getPaint(change_color);
    }

    /**
     * 根据颜色获取对应的画笔
     */
    private Paint getPaint(int color) {
        final Paint paint = new Paint();
        paint.setColor(color);
        Logger.e("textSize = " + getTextSize());
        setTextSize(getTextSize());
        paint.setAntiAlias(true);
        paint.setDither(true);
        return paint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        final String text = getText().toString();
        if (Utils.isNull(text)) return;
        final Rect rect = new Rect();
        originalPaint.getTextBounds(text, 0, text.length(), rect);
        final int dy = (rect.bottom - rect.top) / 2 - rect.bottom;
        final int baseLine = getHeight() / 2 + dy;
        final int startX = getWidth() / 2 - rect.width() / 2;
        canvas.drawText(text, startX, baseLine, originalPaint);

        canvas.restore();
    }
}
