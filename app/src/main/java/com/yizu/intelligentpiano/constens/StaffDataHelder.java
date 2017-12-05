package com.yizu.intelligentpiano.constens;

import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.Measure;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/12/5.
 * Email: chenhuixueba@163.com
 * 处理五线谱数据的帮助类
 */

public class StaffDataHelder {
    private static StaffDataHelder instence;
    //五线谱的属性信息
    private Attributess mAttributess;
    //第一条线的数据
    private List<Measure> mFristStaffData;
    //第二条线的数据
    private List<Measure> mSecondStaffData;
    //保存绘制瀑布流的数据
    private List<PullData> pullData;

    private StaffDataHelder() {
    }

    public static synchronized StaffDataHelder getInstence() {
        if (instence == null) {
            instence = new StaffDataHelder();
        }
        return instence;
    }

    public void AnalyticStaffData(List<Measure> list, IFinish iFinish) {
        init();
        iFinish.success();
    }

    /**
     * 初始化数据
     */
    private void init() {
        if (mFristStaffData != null) {
            mFristStaffData.clear();
        } else {
            mFristStaffData = new ArrayList<>();
        }
        if (mSecondStaffData != null) {
            mSecondStaffData.clear();
        } else {
            mSecondStaffData = new ArrayList<>();
        }
        //初始化瀑布流的数据
        if (pullData == null) {
            pullData = new ArrayList<>();
        } else {
            pullData.clear();
        }
    }
}
