package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/12/11.
 * Email: chenhuixueba@163.com
 */

public class StaffJump {
    //五线谱进度条的位置
    private float posiotion = 0f;
    //该小结音符之前的druction之和
    private int allDruction = 0;
    //该音符的音长
    private int druction = 0;

    public StaffJump(float posiotion, int allDruction, int druction) {
        this.posiotion = posiotion;
        this.allDruction = allDruction;
        this.druction = druction;
    }

    public float getPosiotion() {
        return posiotion;
    }

    public void setPosiotion(float posiotion) {
        this.posiotion = posiotion;
    }

    public int getAllDruction() {
        return allDruction;
    }

    public void setAllDruction(int allDruction) {
        this.allDruction = allDruction;
    }

    public int getDruction() {
        return druction;
    }

    public void setDruction(int druction) {
        this.druction = druction;
    }
}
