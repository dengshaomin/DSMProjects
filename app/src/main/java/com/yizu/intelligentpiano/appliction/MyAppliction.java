package com.yizu.intelligentpiano.appliction;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
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
    public static CloudPushService pushService;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        MyLogUtils.init(true, false);
        OkHttpUtils.getInstance().init(OkHttpUtils.RequestType.HTTP);
        PreManger.instance().init(mContext);
        initCloudChannel(mContext);
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                MyLogUtils.d(TAG, "云推送通道初始化成功");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                MyLogUtils.d(TAG, "云推送通道初始化失败 -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }


}
