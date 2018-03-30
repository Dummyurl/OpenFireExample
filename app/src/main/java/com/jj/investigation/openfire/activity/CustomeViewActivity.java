package com.jj.investigation.openfire.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;

import com.jj.investigation.openfire.R;
import com.jj.investigation.openfire.view.customview.QQStepView;

/**
 * 测试自定义View的页面
 * Created by ${R.js} on 2018/2/9.
 */

public class CustomeViewActivity extends AppCompatActivity {

    private QQStepView stepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custome_view);

        initView();
        stepViewAnim();

    }

    private void initView() {
        stepView = (QQStepView) findViewById(R.id.stepView);
    }

    /**
     * StepView的动画
     */
    private void stepViewAnim() {
        // 添加属性动画，变化值从0-3000变化，该值代表具体的步数
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 3000);
        valueAnimator.setDuration(1500);
        // 添加插值器，开始快，后面慢
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 取出变化的值，把他作为当前的步数，不断的设置给QQStepView，达到动画的目的
                float currentSteps = (float) animation.getAnimatedValue();
                stepView.setCurrentSteps((int) currentSteps);
            }
        });
        valueAnimator.start();
    }

}
