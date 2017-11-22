package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/11/16.
 * Email: chenhuixueba@163.com
 */

public class HeadData{
    float left1;
    float top1;
    float right1;
    float bottom1;
    float px;
    float py;
    float left2;
    float top2;
    float right2;
    float bottom2;

    public HeadData(float left1, float top1, float right1, float bottom1, float px, float py, float left2, float top2, float right2, float bottom2) {
        this.left1 = left1;
        this.top1 = top1;
        this.right1 = right1;
        this.bottom1 = bottom1;
        this.px = px;
        this.py = py;
        this.left2 = left2;
        this.top2 = top2;
        this.right2 = right2;
        this.bottom2 = bottom2;
    }

    public HeadData(float left1, float top1, float right1, float bottom1, float px, float py) {
        this.left1 = left1;
        this.top1 = top1;
        this.right1 = right1;
        this.bottom1 = bottom1;
        this.px = px;
        this.py = py;
    }

    public float getLeft1() {
        return left1;
    }

    public float getTop1() {
        return top1;
    }

    public float getRight1() {
        return right1;
    }

    public float getBottom1() {
        return bottom1;
    }

    public float getPx() {
        return px;
    }

    public float getPy() {
        return py;
    }

    public float getLeft2() {
        return left2;
    }

    public float getTop2() {
        return top2;
    }

    public float getRight2() {
        return right2;
    }

    public float getBottom2() {
        return bottom2;
    }
}
