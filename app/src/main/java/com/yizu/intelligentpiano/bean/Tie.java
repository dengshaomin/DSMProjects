package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/25.
 * All Rights Reserved by YiZu
 */

public class Tie {
    private float x;
    private float y;
    private boolean isUp;

    public Tie(float x, float y, boolean isUp) {
        this.x = x;
        this.y = y;
        this.isUp = isUp;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }
}
