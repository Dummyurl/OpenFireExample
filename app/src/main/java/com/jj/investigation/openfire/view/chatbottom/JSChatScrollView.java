package com.jj.investigation.openfire.view.chatbottom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;

import com.jj.investigation.openfire.utils.Logger;
import com.jj.investigation.openfire.utils.Utils;

/**
 * 可以左右滑动切花页面
 * Created by ${R.js} on 2018/1/27.
 */

public class JSChatScrollView extends LinearLayout {

    // 当前显示的ScrollView的第几页
    private int currentPage = 1;
    // ScrollView最多有几页
    private int maxPage = 1;
    // 屏幕宽度
    private int screenWidth;


    public JSChatScrollView(Context context) {
        this(context, null);
    }

    public JSChatScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JSChatScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = Utils.getScreenWidth(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int downX = 0;
        int moveX = 0;
        int distance = 0;
        Logger.e("screenWidth = " + screenWidth);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                distance = moveX - downX;

                break;
        }
        return true;
    }

    /**
     * 设置该View的宽度
     */
    public void setWidth(int width) {
        maxPage = width / screenWidth;
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = width;
        this.setLayoutParams(layoutParams);
    }

    /**
     * 执行动画
     *
     * @param where 整体布局移动到哪里，而不是移动的距离
     */
    private void runAnimate(int where) {
        ViewPropertyAnimator animate = this.getRootView().animate();
        animate.translationX(where);
        animate.setDuration(500);
        animate.start();
    }
}
