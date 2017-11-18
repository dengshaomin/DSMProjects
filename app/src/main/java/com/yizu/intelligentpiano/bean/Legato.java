package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/25.
 * All Rights Reserved by YiZu
 */

public class Legato {
    private float startX = 0;
    private float stopX = 0;
    private int startY = 0;
    private int stopY = 0;

    public Legato(float startX, int startY, float stopX, int stopY) {
        this.startX = startX;
        this.stopX = stopX;
        this.startY = startY;
        this.stopY = stopY;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(int stopX) {
        this.stopX = stopX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStopY() {
        return stopY;
    }

    public void setStopY(int stopY) {
        this.stopY = stopY;
    }
}
