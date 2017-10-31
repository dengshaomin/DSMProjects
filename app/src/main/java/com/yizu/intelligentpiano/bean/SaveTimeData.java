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
    //-1：白键左面黑键，1右面黑键
    private int black = 0;

    //pullview使用,当前处于正确状态的按钮
    private boolean pressCorrect = false;
    //对应的物理keycode
    private Integer physicalKey;
    //是否已经被记录
    private boolean hasRecord;


    public SaveTimeData(int mAddDuration, int duration, int octave, String step, int black) {
        this.mAddDuration = mAddDuration;
        this.duration = duration;
        this.octave = octave;
        this.step = step;
        this.black = black;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPressCorrect() {
        return pressCorrect;
    }

    public void setPressCorrect(boolean pressCorrect) {
        this.pressCorrect = pressCorrect;
    }


    public Integer getPhysicalKey() {
        return physicalKey;
    }

    public void setPhysicalKey(Integer physicalKey) {
        this.physicalKey = physicalKey;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    public int getBlackNum() {
        return black;
    }

    public void setBlackNum(int black) {
        this.black = black;
    }
}
