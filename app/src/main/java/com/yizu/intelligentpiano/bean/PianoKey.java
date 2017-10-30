package com.yizu.intelligentpiano.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by liuxiaozhu on 2017/10/10.
 * All Rights Reserved by YiZu
 */

public class PianoKey {
    //类型[黑键、白键]
    private Piano.PianoKeyType type;
    //键
    private Drawable keyDrawable;


    public Drawable getKeyDrawable() {
        return keyDrawable;
    }

    public void setKeyDrawable(Drawable keyDrawable) {
        this.keyDrawable = keyDrawable;
    }

    public Piano.PianoKeyType getType() {
        return type;
    }

    public void setType(Piano.PianoKeyType type) {
        this.type = type;
    }
}
