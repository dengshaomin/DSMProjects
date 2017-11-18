package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/11/16.
 * Email: chenhuixueba@163.com
 */

public class Tia {
    private float left;
    private float top;
    private float x;
    private float y;
    private float right;
    private float bottom;

    public Tia(float left, float top, float x, float y, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.x = x;
        this.y = y;
        this.right = right;
        this.bottom = bottom;
    }

    public float getLefts() {
        return left;
    }

    public float getTops() {
        return top;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRightss() {
        return right;
    }

    public float getBottoms() {
        return bottom;
    }
}
