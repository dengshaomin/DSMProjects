package com.yizu.intelligentpiano.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.view.MainActivity;

/**
 * Created by liuxiaozhu on 2017/9/25.
 * All Rights Reserved by YiZu
 * app自启动广播
 */

public class AppStartReceiver extends BroadcastReceiver {
    private static final String TAG= "AppStartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
            MyLogUtils.e(TAG,"APP已启动");
            return;
        }
    }
}
