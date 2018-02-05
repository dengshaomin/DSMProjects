package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2017/12/19.
 * Email: chenhuixueba@163.com
 */

public class RiseRecord {
    //A-B音符
    private String step;
    //组数
    private String octave;
    //升降
    private int alter;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getOctave() {
        return octave;
    }

    public void setOctave(String octave) {
        this.octave = octave;
    }

    public int getAlter() {
        return alter;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public RiseRecord(String step, String octave, int alter) {
        this.step = step;
        this.octave = octave;
        this.alter = alter;
    }
}
