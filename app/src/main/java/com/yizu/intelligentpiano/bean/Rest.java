package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/11/17.
 * Email: chenhuixueba@163.com
 */

public class Rest {
    float left;
    float top;
    float right;
    float bottom;

    public Rest(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }
}
