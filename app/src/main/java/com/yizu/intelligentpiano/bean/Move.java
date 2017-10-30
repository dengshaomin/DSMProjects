package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/20.
 * All Rights Reserved by YiZu
 */

public class Move {
    //音符的duration
    private int duration;
    //长度
    private int lenth;
    //该音符的小节数0 - ...
    private int measure;


    public Move(int duration, int lenth) {
        this.duration = duration;
        this.lenth = lenth;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLenth() {
        return lenth;
    }

    public void setLenth(int lenth) {
        this.lenth = lenth;
    }
}
