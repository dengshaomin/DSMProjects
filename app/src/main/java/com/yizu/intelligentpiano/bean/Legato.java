package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/25.
 * All Rights Reserved by YiZu
 */

public class Legato {
    private float startX = 0;
    private float startY = 0;
    private float stopY = 0;

    public Legato(float startX, float startY, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopY = stopY;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }
}
