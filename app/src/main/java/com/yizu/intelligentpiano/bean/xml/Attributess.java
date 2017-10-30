package com.yizu.intelligentpiano.bean.xml;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/8.
 * All Rights Reserved by YiZu
 */

public class Attributess {
    private String divisions;
    private AttributessKey key;
    private AttributessTime time;
    private String staves;
    private List<Clef> clefList;

    public String getDivisions() {
        return divisions;
    }

    public void setDivisions(String divisions) {
        this.divisions = divisions;
    }

    public AttributessKey getKey() {
        return key;
    }

    public void setKey(AttributessKey key) {
        this.key = key;
    }

    public AttributessTime getTime() {
        return time;
    }

    public void setTime(AttributessTime time) {
        this.time = time;
    }

    public String getStaves() {
        return staves;
    }

    public void setStaves(String staves) {
        this.staves = staves;
    }

    public List<Clef> getClefList() {
        return clefList;
    }

    public void setClefList(List<Clef> clefList) {
        this.clefList = clefList;
    }
}
