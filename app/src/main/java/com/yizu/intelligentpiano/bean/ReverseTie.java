package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2018/1/10.
 * Email: chenhuixueba@163.com
 */

public class ReverseTie {
    private float x1 = 0f;
    private float y1 = 0f;
    private boolean isUp1 = false;
    private float x2 = 0f;
    private float y2 = 0f;
    private boolean isUp2 = false;

    public ReverseTie(float x1, float y1, boolean isUp1,
                      float x2, float y2, boolean isUp2) {
        this.x1 = x1;
        this.y1 = y1;
        this.isUp1 = isUp1;
        this.x2 = x2;
        this.y2 = y2;
        this.isUp2 = isUp2;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public boolean isUp1() {
        return isUp1;
    }

    public void setUp1(boolean up1) {
        isUp1 = up1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public boolean isUp2() {
        return isUp2;
    }

    public void setUp2(boolean up2) {
        isUp2 = up2;
    }
}
