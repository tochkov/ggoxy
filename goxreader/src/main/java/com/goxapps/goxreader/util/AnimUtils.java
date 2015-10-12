package com.goxapps.goxreader.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by tochkov.
 */
public class AnimUtils {


    public static void animateViewAndHide(final View viewToAnimate){
        Animation anim = new AlphaAnimation(1,0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewToAnimate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        viewToAnimate.startAnimation(anim);
    }

    public static void animateViewAndShow(final View viewToAnimate){
        Animation anim = new AlphaAnimation(0,1);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewToAnimate.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        viewToAnimate.startAnimation(anim);
    }

}
