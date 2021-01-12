package com.trianguloy.isUserAMonkey.tools;

import android.animation.LayoutTransition;
import android.os.Build;
import android.view.ViewGroup;

/**
 * Enables the animations for changing components
 */
public class Animations {
    static public void enable(ViewGroup view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
    }
}