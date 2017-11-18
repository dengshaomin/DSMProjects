package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/11/17.
 * Email: chenhuixueba@163.com
 */

public class Dwon {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float left;
    private float top;
    private float x;
    private float y;
    private float right;
    private float bottom;

    public Dwon(float x1, float y1, float x2, float y2, float left, float top, float x, float y, float right, float bottom) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.left = left;
        this.top = top;
        this.x = x;
        this.y = y;
        this.right = right;
        this.bottom = bottom;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }
}
