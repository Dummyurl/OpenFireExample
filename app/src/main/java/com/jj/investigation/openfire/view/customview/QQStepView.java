package com.jj.investigation.openfire.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.utils.Utils;

/**
 * 魔方QQ显示步数的View
 * Created by ${R.js} on 2018/2/8.
 */

public class QQStepView extends View {

    private Context context;
    // view默认的大小
    private int viewSize = 200;
    private int leftColor = Color.RED;
    private int rightColor = Color.BLUE;
    private int borderWidth = 10;
    private int textColor = getContext().getResources().getColor(R.color.red_f15353);
    private int textSize = 16;
    private String text;
    private Paint leftPaint, rightPaint, textPaint;
    // 最大的步数
    private int maxSteps = 5000;
    // 当前的步数
    private int currentSteps = 0;

    public QQStepView(Context context) {
        this(context, null);
    }

    public QQStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        obtainAttrs(attrs);
        initPaint();
    }

    /**
     * leftPaint.setStrokeCap(Paint.Cap.ROUND);的解释：
     * 画圆弧时，如果线条有一定的宽度，则内外链接的时候是直线，画出来的弧度不好看，加上该
     * 行代码后，内外连线是一个圆弧，比较圆润
     */
    private void initPaint() {
        leftPaint = new Paint();
        leftPaint.setAntiAlias(true);
        leftPaint.setStrokeWidth(borderWidth);
        leftPaint.setColor(leftColor);
        leftPaint.setStrokeCap(Paint.Cap.ROUND);
        leftPaint.setStyle(Paint.Style.STROKE);


        rightPaint = new Paint();
        rightPaint.setAntiAlias(true);
        rightPaint.setStrokeWidth(borderWidth);
        rightPaint.setColor(rightColor);
        leftPaint.setStrokeCap(Paint.Cap.ROUND);
        rightPaint.setStyle(Paint.Style.STROKE);


        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     * 获取自定义属性
     */
    private void obtainAttrs(AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        leftColor = typedArray.getColor(R.styleable.QQStepView_leftColor, leftColor);
        rightColor = typedArray.getColor(R.styleable.QQStepView_rightColor, rightColor);
        textColor = typedArray.getColor(R.styleable.QQStepView_stepTextColor, textColor);
        borderWidth = (int) typedArray.getDimension(R.styleable.QQStepView_circleBorder, borderWidth);
        textSize = typedArray.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize, textSize);
        text = typedArray.getString(R.styleable.QQStepView_stepText);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        // 1.画右侧大圆弧
        final RectF rectRight = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getHeight());
        canvas.drawArc(rectRight, 135, 270, false, rightPaint);

        // 2.画左侧小圆弧
        if (currentSteps == 0) return;
        if (Utils.isNull(text)) return;
        float progress = (float) currentSteps / maxSteps;
        final RectF rectLeft = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getHeight());
        canvas.drawArc(rectLeft, 135, 270 * progress, false, leftPaint);

        // 3.画圆内文字
        final Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        final int dy = (textRect.bottom - textRect.top) / 2 - textRect.bottom;
        final int baseLine = getHeight() / 2 + dy;
        final int dx = getWidth() / 2 - textRect.width() / 2;
        canvas.drawText(text, dx, baseLine, textPaint);
    }

    /**
     * 设置最大步数
     */
    public synchronized void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    /**
     * 设置当前步数
     */
    public synchronized void setCurrentSteps(int currentSteps) {
        if (currentSteps > maxSteps)
            maxSteps = currentSteps;
        this.currentSteps = currentSteps;
        text = currentSteps + "";
        // 只要当前步数发生改变，就刷新，即调用onDraw方法
        invalidate();
    }

}
