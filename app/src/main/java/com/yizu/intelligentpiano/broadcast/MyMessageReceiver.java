package com.yizu.intelligentpiano.broadcast;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.ILogin;
import com.yizu.intelligentpiano.constens.ILogout;
import com.yizu.intelligentpiano.constens.IMusic;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.Map;

public class MyMessageReceiver extends MessageReceiver {
    public static final String TAG = "MyMessageReceiver";

    private static ILogin mLogin;
    private static IMusic mMusic;
    private static ILogout mLogout;

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        MyLogUtils.e(TAG, "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
        switch (summary) {
            case "扫码登录":
                //登陆
                if (mLogin != null) {
                    mLogin.login(extraMap);
                }
                break;
            case "音乐推送":
                //音乐推送
                if (mMusic != null) {
                    mMusic.music(extraMap);
                }
                break;
            case "退出登录":
                //小程序退出
                if (mLogout != null) {
                    Constents.user_id = extraMap.get("user_id");
                    mLogout.logout();
                }
                break;
        }
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        MyLogUtils.e(TAG, "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        MyLogUtils.e(TAG, "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        MyLogUtils.e(TAG, "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        MyLogUtils.e(TAG, "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        MyLogUtils.e(TAG, "onNotificationRemoved");
    }

    /**
     * 获取登陆
     *
     * @param login
     */
    public static void getLogin(ILogin login) {
        if (login == null) {
            throw new RuntimeException("ILogin不能为空");
        }
        mLogin = login;
    }

    /**
     * 推送
     *
     * @param music
     */
    public static void getMusic(IMusic music) {
        if (music == null) {
            throw new RuntimeException("IMusic不能为空");
        }
        mMusic = music;
    }

    /**
     * 退出
     *
     * @param logout
     */
    public static void getLogout(ILogout logout) {
        if (logout == null) {
            throw new RuntimeException("ILogout不能为空");
        }
        mLogout = logout;
    }


}
