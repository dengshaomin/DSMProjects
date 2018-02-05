package com.yizu.intelligentpiano.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.OkHttpUtils;

/**
 * Author：Created by liuxiaozhu on 2017/11/24.
 * Email: chenhuixueba@163.com
 */

public class NetChangeReceiver extends BroadcastReceiver {
    private final static String TAG = "NetChangeReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobInfo = manger.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manger.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobInfo.isConnected()) {
            OkHttpUtils.getInstance().startWebSocket();
        } else if (wifiInfo.isConnected()) {
            MyLogUtils.e(TAG, "WIFI连接");
            OkHttpUtils.getInstance().startWebSocket();
        } else {
            MyLogUtils.e(TAG, "网络断开");
            OkHttpUtils.getInstance().webCancle();
        }
    }
}
