package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/25.
 * All Rights Reserved by YiZu
 */

public class Legato {
    private String number;
    private int x;
    private int y;

    public Legato(String number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
