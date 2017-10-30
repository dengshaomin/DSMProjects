package com.yizu.intelligentpiano.bean;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/10/23.
 * All Rights Reserved by YiZu
 */

public class PullData {
    private List<SaveTimeData> frist;
    private List<SaveTimeData> second;

    public PullData(List<SaveTimeData> frist, List<SaveTimeData> second) {
        this.frist = frist;
        this.second = second;
    }

    public List<SaveTimeData> getFrist() {
        return frist;
    }

    public void setFrist(List<SaveTimeData> frist) {
        this.frist = frist;
    }

    public List<SaveTimeData> getSecond() {
        return second;
    }

    public void setSecond(List<SaveTimeData> second) {
        this.second = second;
    }
}
