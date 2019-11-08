package com.afscope.autoredbacket;

import android.content.Context;

public class ScreenUtils {
    /**
     * 获得屏幕的宽度
     * @param context
     * @return
     */
    public static int getWindowsWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得屏幕的高度
     */
    public static int getWindowsHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获得屏幕的dpi
     */
    public static int getDensityDpit(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }
}
