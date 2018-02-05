package com.yizu.intelligentpiano.utils;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.appliction.MyAppliction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenhui on 2017/6/24.
 * All Rights Reserved by YiZu
 * Log工具类
 */

public class MyLogUtils {
    //是否打印Log,默认不打印
    private static boolean ISDEBUG = false;
    private final static boolean isSaveLog = false;
    private final static String Tag = "MyLog-";
    private static String mPath;


    /**
     * 初始化Log
     */
    public static void init() {
        ApplicationInfo info = MyAppliction.getContext().getApplicationInfo();
        ISDEBUG = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (isSaveLog) {
            String string = SDCardUtils.getExternalStorageDirectory();
            if (!string.equals("")) {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
//                mPath = string + "/" + MyAppliction.getContext().getString(R.string.app_name).concat("/") + str.concat(".txt");
                mPath = string + "/" + MyAppliction.getContext().getString(R.string.app_name).concat(".txt");
            }
        }
    }

    public static void e(String tag, String msg) {
        if (ISDEBUG) {
            Log.e(Tag.concat(tag), msg);
            if (isSaveLog && mPath != null) {
                startWriteThread(" /E：" + tag.concat(": " + msg + "\n"));
            }
        }
    }

    public static void d(String tag, String msg) {
        if (ISDEBUG) {
            Log.d(Tag.concat(tag), msg);
            if (isSaveLog && mPath != null) {
                startWriteThread(" /D：" + tag.concat(": " + msg + "\n"));
            }
        }
    }

    public static void i(String tag, String msg) {
        if (ISDEBUG) {
            Log.i(Tag.concat(tag), msg);
            if (isSaveLog && mPath != null) {
                startWriteThread(" /I：" + tag.concat(": " + msg + "\n"));
            }
        }
    }

    public static void w(String tag, String msg) {
        if (ISDEBUG) {
            Log.w(Tag.concat(tag), msg);
            if (isSaveLog && mPath != null) {
                startWriteThread(" /W：" + tag.concat(": " + msg + "\n"));
            }
        }
    }

    /**
     * 将log信息输出
     *
     * @param systemOuts
     */
    private synchronized static void startWriteThread(final String systemOuts) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        final String inputLogInfo = str.concat(systemOuts);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPath == null || mPath.equals("")) return;
                File file = new File(mPath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream outputStream = null;
                byte[] b = inputLogInfo.getBytes();
                try {
                    outputStream = new FileOutputStream(file, true);
                    outputStream.write(b, 0, b.length);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
