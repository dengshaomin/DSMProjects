package com.yizu.intelligentpiano.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.INetStatus;
import com.yizu.intelligentpiano.utils.MyLogUtils;

/**
 * Created by liuxiaozhu on 2017/9/26.
 * All Rights Reserved by YiZu
 * 监听网络是否变化
 */

public class TimeChangeReceiver extends BroadcastReceiver {
    private final static String TAG = "TimeChangeReceiver";
    private static INetStatus mINetStatus;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobInfo = manger.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manger.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobInfo.isConnected()) {
            Constents.isNetworkConnected = true;
            MyLogUtils.e(TAG,"移动网络连接");
        } else if (wifiInfo.isConnected()) {
            Constents.isNetworkConnected = true;
            MyLogUtils.e(TAG,"WIFI连接");
        } else {
            MyLogUtils.e(TAG,"网络断开");
            Constents.isNetworkConnected = false;
            if (mINetStatus != null) {
                mINetStatus.isNoNet();
            }
        }
    }
    public static void getNetStatus(INetStatus netStatus) {
        if (netStatus != null) {
            mINetStatus = netStatus;
        }
    }
}
