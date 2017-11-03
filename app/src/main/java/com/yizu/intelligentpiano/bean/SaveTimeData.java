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

    //对应的物理keycode
    private int physicalKey;
    //是否已经被记录
    private boolean hasRecord;
    //    //用于pullview记录到达pullview底部的状态 0:初始状态 1：第一次到达pullview底部 2：正在经过pullview 3:移出pullview
    private int arriveBottomState;
//    private int top;
//    private int bottom;

    private boolean isRest = false;

    private float top;
    private float bottom;
    private float left;
    //是否谱子的最后一个音符
    private boolean isLastNode;

    public boolean isLastNode() {
        return isLastNode;
    }

    public void setLastNode(boolean lastNode) {
        isLastNode = lastNode;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    private float right;
    private boolean hasCac;


    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public boolean isHasCac() {
        return hasCac;
    }

    public void setHasCac(boolean hasCac) {
        this.hasCac = hasCac;
    }

    public SaveTimeData(int mAddDuration, int duration, int octave, String step, int black) {
        this.mAddDuration = mAddDuration;
        this.duration = duration;
        this.octave = octave;
        this.step = step;
        this.black = black;
    }

    /**
     * 保存休止符号
     *
     * @param mAddDuration
     * @param duration
     * @param isRest
     */
    public SaveTimeData(int mAddDuration, int duration, boolean isRest) {
        this.mAddDuration = mAddDuration;
        this.duration = duration;
        this.isRest = isRest;
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


    public int getPhysicalKey() {
        return physicalKey;
    }

    public void setPhysicalKey(int physicalKey) {
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

    public boolean isRest() {
        return isRest;
    }

    public void setRest(boolean rest) {
        isRest = rest;
    }

    public int getBlack() {
        return black;
    }

    public void setBlack(int black) {
        this.black = black;
    }

    public int getArriveBottomState() {
        return arriveBottomState;
    }

    public void setArriveBottomState(int arriveBottomState) {
        this.arriveBottomState = arriveBottomState;
    }

//    public int getTop() {
//        return top;
//    }
//
//    public void setTop(int top) {
//        this.top = top;
//    }
//
//    public int getBottom() {
//        return bottom;
//    }
//
//    public void setBottom(int bottom) {
//        this.bottom = bottom;
//    }
}
