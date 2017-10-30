package com.yizu.intelligentpiano.constens;

/**
 * Created by liuxiaozhu on 2017/9/22.
 * All Rights Reserved by YiZu
 */

public interface HttpUrls {
    String HTTPBASE = "http://piano.sinotransfer.com";
    //APP升级
    String GETAPPVERSION = "/Music/getApp";
    //获取二维码
    String QRCODE = "/Weichat/getQrcode";
    //登陆
    String LOGIN = "/Music/login";
    //退出登录
    String LOGOUT = "/Music/logout";
    //获取用户信息
    String GETUSERINFO = "/Music/getUserInfo";
    //获取弹奏记录
    String MUSICHISTORY = "/Music/getMusicHistory";
    //获取歌曲列表
    String GETCATEGORY = "/Music/getCategory";
    //视频
    String GETVIDEOLIST = "/Music/getVideoList";
    //歌曲分类
    String GETLIST = "/Music/getList";
    //打分记录上传
    String addMusicHistory = "/Music/addMusicHistory  ";

}
