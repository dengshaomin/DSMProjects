package com.yizu.intelligentpiano.constens;

/**
 * Created by liuxiaozhu on 2017/9/25.
 * All Rights Reserved by YiZu
 */

public class Constents {
    //是否联网，true为联网
    public static boolean isNetworkConnected = true;
    public static String user_id = "";
    //广播标识
    public static final String ACTION = "action";
    public static final String KEY = "what";
    //广播 app请求退出
    public static final String LOGOUT = "logout";
    //广播 activity可以退出
    public static final String LOGOUT_FINISH = "finish";
    //广播 音乐推送
    public static final String MUSIC = "music";
    //广播 剩余5分钟
    public static final String NOTIME_5 = "5";

    //sd卡文件路径
    public static final String PIANO_URL = "/智能钢琴";
    public static final String VIDEO_URL = PIANO_URL.concat("/video");
    public static final String APK_URL = PIANO_URL.concat("/apk");

    public static final String XML_CHILDREN = PIANO_URL.concat("/儿童");
    public static final String XML_SATINE = PIANO_URL.concat("/金典");
    public static final String XML_NOSTALGIC = PIANO_URL.concat("/怀古");
    public static final String XML_POPULAR = PIANO_URL.concat("/流行");
    public static final String XML_GAME = PIANO_URL.concat("/动漫游戏");
    public static final String XML_SENTIMENTAL = PIANO_URL.concat("/伤感");

}
