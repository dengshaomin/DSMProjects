package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Piano;
import com.yizu.intelligentpiano.bean.PianoKey;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuxiaozhu on 2017/10/9.
 * All Rights Reserved by YiZu
 */

public class PianoKeyView extends View {
    private static final String TAG = "PianoKeyView";
    public final static int WHITE_PIANO_KEY_NUMS = 52;

    private Context mContext;

    private Piano mPiano;

    private int mLayoutWidth;
    private int mLayoutHeight;


    private int mBlackKeyWidth;
    private int mBlackKeyHeight;
    private int mWhiteKeyWidth;
    private int mWhiteKeyHeight;

    private float scaleWidth;
    private float scaleHeight;


    private ArrayList<PianoKey[]> whitePianoKeys;
    //
    private ArrayList<PianoKey[]> blackPianoKeys;
    //
    private List<PianoKey> allPianoKeys;

    public PianoKeyView(Context context) {
        this(context, null);
    }

    public PianoKeyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianoKeyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.white_piano_key);
        Drawable blackKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.black_piano_key);
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int whiteKeyWidth = whiteKeyDrawable.getIntrinsicWidth();
        int blackKeyWidth = blackKeyDrawable.getIntrinsicWidth();
        int blackKeyHeight = blackKeyDrawable.getIntrinsicHeight();
        float scale = (float) whiteKeyHeight / whiteKeyWidth;

        mWhiteKeyWidth = (mLayoutWidth + 60) / WHITE_PIANO_KEY_NUMS;

        mLayoutHeight = (int) ((mWhiteKeyWidth * scale * 10 + 9) / 10);
        mWhiteKeyHeight = mLayoutHeight;
        scaleHeight = (float) mWhiteKeyHeight / whiteKeyHeight;
        scaleWidth = (float) mWhiteKeyWidth / whiteKeyWidth;
        mBlackKeyHeight = (int) (blackKeyHeight * scaleHeight);
        mBlackKeyWidth = (int) (blackKeyWidth * scaleWidth);
        setMeasuredDimension(mLayoutWidth, mLayoutHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        MyLogUtils.e(TAG, "onDraw");
        if (mPiano == null) {
            mPiano = new Piano(mContext, mBlackKeyHeight, mBlackKeyWidth,
                    mWhiteKeyHeight, mWhiteKeyWidth, scaleHeight, scaleWidth);
//
            whitePianoKeys = mPiano.getWhitePianoKeys();
//            //
            blackPianoKeys = mPiano.getBlackPianoKeys();
            //
            allPianoKeys = mPiano.getKeyList();
        }
//
        for (int i = 0; i < whitePianoKeys.size(); i++) {
            for (PianoKey key : whitePianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
        //
        for (int i = 0; i < blackPianoKeys.size(); i++) {
            for (PianoKey key : blackPianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
    }


    public void painoKeyPress(int prass) {
        allPianoKeys.get(prass - 21).getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        invalidate(allPianoKeys.get(prass - 21).getKeyDrawable().getBounds());
    }

    public void painoKeyCanclePress(int cancle) {
        allPianoKeys.get(cancle - 21).getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
        invalidate(allPianoKeys.get(cancle - 21).getKeyDrawable().getBounds());
    }

    /**
     * 获取白键宽度
     *
     * @return
     */
    public int getmWhiteKeyWidth() {
        return mWhiteKeyWidth;
    }

    /**
     * 获取黑键宽度
     *
     * @return
     */
    public int getmBlackKeyWidth() {
        return mBlackKeyWidth;
    }
}
