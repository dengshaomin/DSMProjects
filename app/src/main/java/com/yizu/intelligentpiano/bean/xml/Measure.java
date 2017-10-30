package com.yizu.intelligentpiano.bean.xml;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/8.
 * All Rights Reserved by YiZu
 * 乐谱
 */

public class Measure {
    private String number;
    private List<MeasureBase> note;

    public Measure() {
    }

    public Measure(List<MeasureBase> note) {
        this.note = note;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public List<MeasureBase> getMeasure() {
        return note;
    }

    public void setMeasure(List<MeasureBase> note) {
        this.note = note;
    }
}
