package com.yizu.intelligentpiano.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.yizu.intelligentpiano.appliction.MyAppliction;

/**
 * Created by chenhui on 2017/6/22.
 * All Rights Reserved by YiZu
 * 这是一个通用的Toast工具类，防止连续点击造成连续不断的弹出
 */

public class MyToast{
    private static Toast mToast;
    private final static Context mContext = MyAppliction.getContext();

    /**
     * 长时间显示
     * @param resId
     */
    public static void ShowLong(@StringRes int resId) {
        if (isShow()) {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_LONG);
        }else{
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    /**
     * 长时间显示
     * @param text
     */
    public static void ShowLong(CharSequence text) {
        if (isShow()) {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }else{
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    /**
     * 短时间显示
     * @param resId
     */
    public static void ShowShort( @StringRes int resId) {
        if (isShow()) {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }else{
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 短时间显示
     * @param text
     */
    public static void ShowShort(CharSequence text) {
        if (isShow()) {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }else{
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 判断当前Toast是否存在
     * @return
     */
    private static boolean isShow() {
        if (mToast == null) {
            return false;
        } else {
            return true;
        }
    }
}
