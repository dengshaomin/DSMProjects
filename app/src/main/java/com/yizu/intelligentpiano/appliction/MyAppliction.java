package com.yizu.intelligentpiano.appliction;

import android.app.Application;
import android.content.Context;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;

/**
 * Created by liuxiaozhu on 2017/9/19.
 * All Rights Reserved by YiZu
 */

public class MyAppliction extends Application {
    private static final String TAG = "MyAppliction";
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        MyLogUtils.init();
        OkHttpUtils.getInstance().init(OkHttpUtils.RequestType.HTTP);
        PreManger.instance().init(mContext);
    }

    public static Context getContext() {
        return mContext;
    }



}
