package com.xlibs.xrv.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * the screen tool, include get the screen's width, height, dip to px, px to dip
 * Created by cherish on 2016/4/18.
 */
public class ScreenUtil {
    /**
     * get screen width
     */
    public static int getScreenWidth(Context context){
        DisplayMetrics dm = getDisplayMetrics(context);

        if(dm != null) {
            return dm.widthPixels;
        }
        else{
            return 0;
        }
    }
    /**
     * get DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context){
        if(context == null){
            return null;
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
    /**
     * dip to pixel
     *
     * @param context context
     * @param dipValue dp value
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        if(context == null){
            return 0;
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * pixel to dip
     *
     * @param context context
     * @param pxValue px value
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        if(context == null){
            return 0;
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
