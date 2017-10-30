package com.yizu.intelligentpiano.bean.xml;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/8.
 * All Rights Reserved by YiZu
 * 音符
 */

public class Notes {
    private Pitch pitch;
    private String duration;
    private String voice;
    private String type;
    private String stem;
    private String staff;
    //和弦，是和前一个音符一起弹奏
    private boolean isChords = false;
    //休止符
    private boolean rest = false;
    //半音
    private boolean dot = false;
    private List<Beam> beam;

    private List<String> tie;//黑色延音线 start ，stop 弧线
    private List<String> slur;//红色延音线 start ，stop 弧线


    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStems() {
        return stem;
    }

    public void setStems(String stem) {
        this.stem = stem;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public boolean getDot() {
        return dot;
    }

    public void setDots(boolean dot) {
        this.dot = dot;
    }

    public boolean getRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public boolean getChord() {
        return isChords;
    }

    public void setChords(boolean chords) {
        isChords = chords;
    }

    public List<Beam> getBeam() {
        return beam;
    }

    public void setBeam(List<Beam> beam) {
        this.beam = beam;
    }

    public List<String> getTie() {
        return tie;
    }

    public void setTie(List<String> tie) {
        this.tie = tie;
    }

    public List<String> getSlur() {
        return slur;
    }

    public void setSlur(List<String> slur) {
        this.slur = slur;
    }
}
