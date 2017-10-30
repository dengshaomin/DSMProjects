package com.yizu.intelligentpiano.utils;

import android.util.Log;

/**
 * Created by chenhui on 2017/6/24.
 * All Rights Reserved by YiZu
 * Log工具类
 */

public class MyLogUtils {
    //是否打印Log,默认不打印
    public static boolean isPutLog = false;
    private final static String Tag = "MyLog-";
    public static void e(String tag,String msg) {
        if (isPutLog) {
            Log.e(Tag.concat(tag), msg);
        }
    }
    public static void d(String tag,String msg) {
        if (isPutLog) {
            Log.d(Tag.concat(tag), msg);
        }
    }
    public static void i(String tag,String msg) {
        if (isPutLog) {
            Log.i(Tag.concat(tag), msg);
        }
    }
    public static void w(String tag,String msg) {
        if (isPutLog) {
            Log.w(Tag.concat(tag), msg);
        }
    }
}
