package com.isteer.b2c.utility;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.isteer.b2c.R;

/**
 * Created by rnows on 26-Mar-18.
 */

public class SlideAnimationUtil {

    public static void slideInFromLeft(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_from_left);
    }

    public static void slideOutToLeft(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_to_left);
    }


    public static void slideInFromRight(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_from_right);
    }


    public static void slideOutToRight(Context context, View view) {
        runSimpleAnimation(context, view, R.anim.slide_to_right);
    }


    private static void runSimpleAnimation(Context context, View view, int animationId) {
        view.startAnimation(AnimationUtils.loadAnimation(
                context, animationId
        ));
    }

    public static void overridePendingTransition(int slide_from_right, int slide_to_left) {

    }
}
