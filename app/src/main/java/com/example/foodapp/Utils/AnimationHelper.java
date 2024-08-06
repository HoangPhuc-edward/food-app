package com.example.foodapp.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

public class AnimationHelper {
    private final View moveView;
    private final int beginX, beginY;
    private final int endX, endY;

    public AnimationHelper(View moveView, View beginView, View endView){
        this.moveView = moveView;
        int[] location = new int[2];
        beginView.getLocationOnScreen(location);
        this.beginX = location[0];
        this.beginY = location[1];

        endView.getLocationOnScreen(location);
        this.endX = location[0];
        this.endY = location[1];
    }

    public AnimationHelper(View moveView, int beginX, int beginY, int endX, int endY) {
        this.moveView = moveView;
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
    }

    public AnimationHelper(View moveView, int beginX, int beginY, View endView) {
        this.moveView = moveView;
        this.beginX = beginX;
        this.beginY = beginY;

        int[] location = new int[2];
        endView.getLocationOnScreen(location);
        this.endX = location[0];
        this.endY = location[1];
    }

    public void startAnimation(int duration){
        moveView.setVisibility(View.VISIBLE);
        moveView.setX(beginX);
        moveView.setY(beginY);

        ObjectAnimator animationX = ObjectAnimator.ofFloat(moveView, "x", beginX, endX);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(moveView, "y", beginY, endY);

        animationX.setDuration(duration);
        animationY.setDuration(duration);

        animationX.start();
        animationY.start();

        animationY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                moveView.setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });
    }

}
