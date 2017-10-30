package com.yizu.intelligentpiano.bean.xml;

/**
 * Created by liuxiaozhu on 2017/10/24.
 * All Rights Reserved by YiZu
 */

public class Beam {
    private String number;
    //begin开始,end 结束,forward hook 短小向前,backward hook短小向后,continue经过
    private String beam;

    public Beam(String number, String beam) {
        this.number = number;
        this.beam = beam;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBeam() {
        return beam;
    }

    public void setBeam(String beam) {
        this.beam = beam;
    }
}
