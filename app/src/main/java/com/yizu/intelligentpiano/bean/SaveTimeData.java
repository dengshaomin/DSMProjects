package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/10/19.
 * All Rights Reserved by YiZu
 */

public class SaveTimeData {
    //该音符之前的时间和
    private int mAddDuration;
    //该音符的时间
    private int duration;
    //第几组，0-8
    private int octave;
    //音调 CDEFGAB
    private String step;
    private boolean isRest = false;

    public SaveTimeData(int mAddDuration, int duration, int octave, String step) {
        this.mAddDuration = mAddDuration;
        this.duration = duration;
        this.octave = octave;
        this.step = step;
    }

    public int getmAddDuration() {
        return mAddDuration;
    }

    public void setmAddDuration(int mAddDuration) {
        this.mAddDuration = mAddDuration;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public boolean isRest() {
        return isRest;
    }

    public void setRest(boolean rest) {
        isRest = rest;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
