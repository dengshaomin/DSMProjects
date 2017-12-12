package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author：Created by liuxiaozhu on 2017/12/8.
 * Email: chenhuixueba@163.com
 */

public class MyVideoView extends VideoView {
    public MyVideoView(Context context) {
        this(context,null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }
}
