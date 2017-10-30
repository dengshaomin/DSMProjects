package com.yizu.intelligentpiano.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/8/23.
 */
public class PreManger {
    /*PreferenceManger自己的实例*/
    private static PreManger mInstance;
    /*SharedPreferences的实例*/
    private SharedPreferences mPreferences;
    /*SharedPreferences的编辑器*/
    private SharedPreferences.Editor mEditor;

    /*私有化的构造函数，目的是不能在其他类实例化*/
    private PreManger() {

    }

    /*获取当前类的实例*/
    public static PreManger instance() {
        if (mInstance == null) {
            mInstance = new PreManger();
        }
        return mInstance;
    }

    /**
     * 初始化mPreferences  和mEditor
     *
     * @param context：上下文
     */
    public void init(Context context) {
        //
        mPreferences = context.getSharedPreferences("preferenre", Context.MODE_PRIVATE);
        if (mPreferences != null) {
            mEditor = mPreferences.edit();
            mEditor.apply();
        }

    }

    public void saveData(String pic,String status) {
        if (mEditor != null) {
            mEditor.putString("img", pic);
            mEditor.putString("status", status);
            mEditor.commit();
        }
    }
    public String getPic() {
        if (mPreferences != null) {
            return mPreferences.getString("img", "");
        }
        return "";
    }
    public String getStatus() {
        if (mPreferences != null) {
            //默认共享
            return mPreferences.getString("status", "1");
        }
        return "";
    }

    /**
     * 保存手机的mac地址
     * @param macid
     */
    public void saveMacId(String macid) {
        if (mEditor != null) {
            mEditor.putString("macid", macid);
            mEditor.commit();
        }
    }

    /**
     * 获取手机的mac地址
     * @return
     */
    public String getMacId() {
        if (mPreferences != null) {
            return mPreferences.getString("macid", "");
        }
        return "";
    }

    /**
     * 保存手机的mac地址
     * @param userId
     * @param headimg
     * @param nickname
     */
    public void saveUserInfo(String userId, String headimg, String nickname) {
        if (mEditor != null) {
            mEditor.putString("userId", userId);
            mEditor.putString("headimg", headimg);
            mEditor.putString("nickname", nickname);

            mEditor.commit();
        }
    }

    public String getUserID() {
        if (mPreferences != null) {
            return mPreferences.getString("userId", "");
        }
        return "";
    }
}
