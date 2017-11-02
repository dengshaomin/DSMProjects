package com.yizu.intelligentpiano.bean.xml;

/**
 * Created by liuxiaozhu on 2017/9/8.
 * All Rights Reserved by YiZu
 */

public class MeasureBase {
    private Notes notes;
    private Backup backup;
    private Attributess attributes;
    private Barline barline;
    //拍子数
    private String sound;

    //未知属性(里面的属性暂时不知道)
    private Direction direction;
    private Forward forward;
    private Transpose transpose;


    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    public Backup getBackup() {
        return backup;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    public Attributess getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributess attributes) {
        this.attributes = attributes;
    }

    public Barline getBarline() {
        return barline;
    }

    public void setBarline(Barline barline) {
        this.barline = barline;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Forward getForward() {
        return forward;
    }

    public void setForward(Forward forward) {
        this.forward = forward;
    }

    public Transpose getTranspose() {
        return transpose;
    }

    public void setTranspose(Transpose transpose) {
        this.transpose = transpose;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
