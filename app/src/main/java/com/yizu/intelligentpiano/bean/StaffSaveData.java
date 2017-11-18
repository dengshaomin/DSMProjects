package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/11/16.
 * 保存所有绘制音符的信息
 */

public class StaffSaveData {
    private float left;
    private int top;
    private float right;
    private int bottom;

    public StaffSaveData(float left, int top, float right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float getLefts() {
        return left;
    }

    public int getTops() {
        return top;
    }

    public float getRightss() {
        return right;
    }

    public int getBottoms() {
        return bottom;
    }
}
