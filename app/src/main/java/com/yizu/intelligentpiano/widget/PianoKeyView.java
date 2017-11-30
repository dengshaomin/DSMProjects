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
 * 钢琴键盘的处理
 */

public class PianoKeyView extends View {
    private static final String TAG = "PianoKeyView";
    //钢琴键总数目
    private final static int PIANO_NUMS = 88;
    //黑键总数目
    public final static int BLACK_PIANO_KEY_NUMS = 36;
    //白键总数目
    public final static int WHITE_PIANO_KEY_NUMS = 52;

    private Context mContext;
    //定义钢琴键
    private Piano mPiano;
    //布局宽高
    private int mLayoutWidth;
    private int mLayoutHeight;

    //黑白键高度和宽度
    private int mBlackKeyWidth;
    private int mBlackKeyHeight;
    private int mWhiteKeyWidth;
    private int mWhiteKeyHeight;

    private float scaleWidth;
    private float scaleHeight;

    //白键组合
    private ArrayList<PianoKey[]> whitePianoKeys;
    //黑键组合
    private ArrayList<PianoKey[]> blackPianoKeys;
    //黑白键
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
        //获取布局宽度
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.white_piano_key);
        Drawable blackKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.black_piano_key);
        //黑白键的宽高
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int whiteKeyWidth = whiteKeyDrawable.getIntrinsicWidth();
        int blackKeyWidth = blackKeyDrawable.getIntrinsicWidth();
        int blackKeyHeight = blackKeyDrawable.getIntrinsicHeight();
        //根据白键的宽高计算比例
        float scale = (float) whiteKeyHeight / whiteKeyWidth;
        MyLogUtils.e(TAG, "" + scale);
        //白键宽度
        mWhiteKeyWidth = (mLayoutWidth + 60) / WHITE_PIANO_KEY_NUMS;
//        if (mLayoutWidth % WHITE_PIANO_KEY_NUMS != 0) {
//            mWhiteKeyWidth += 1;
//        }
//        计算得出布局的实际高度
        mLayoutHeight = (int) ((mWhiteKeyWidth * scale * 10 + 9) / 10);
        mWhiteKeyHeight = mLayoutHeight;
        scaleHeight = (float) mWhiteKeyHeight / whiteKeyHeight;
        scaleWidth = (float) mWhiteKeyWidth / whiteKeyWidth;
        //计算黑键宽高
        mBlackKeyHeight = (int) (blackKeyHeight * scaleHeight);
        mBlackKeyWidth = (int) (blackKeyWidth * scaleWidth);
        //从新设置布局高度和宽度
        setMeasuredDimension(mLayoutWidth, mLayoutHeight);
        MyLogUtils.e(TAG, mWhiteKeyHeight + "   " + mWhiteKeyWidth + "   " + mBlackKeyHeight + "   " + mBlackKeyWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        MyLogUtils.e(TAG, "onDraw");
//        long time = System.currentTimeMillis();
        if (mPiano == null) {
            mPiano = new Piano(mContext, mBlackKeyHeight, mBlackKeyWidth,
                    mWhiteKeyHeight, mWhiteKeyWidth, scaleHeight, scaleWidth);
//            //获取白键
            whitePianoKeys = mPiano.getWhitePianoKeys();
//            //获取黑键
            blackPianoKeys = mPiano.getBlackPianoKeys();
            //获取所有按顺序排列的组合
            allPianoKeys = mPiano.getKeyList();
        }
//      初始化白键
        for (int i = 0; i < whitePianoKeys.size(); i++) {
            for (PianoKey key : whitePianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
        //初始化黑键
        for (int i = 0; i < blackPianoKeys.size(); i++) {
            for (PianoKey key : blackPianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
//        MyLogUtils.e(TAG, "键盘绘制时间：" + (System.currentTimeMillis() - time));
    }


    public void painoKeyPress(int prass) {

//        实现按压效果
        allPianoKeys.get(prass - 21).getKeyDrawable().setState(new int[]{android.R.attr.state_pressed});
        // 将键重新进行绘制
        invalidate(allPianoKeys.get(prass - 21).getKeyDrawable().getBounds());
    }

    public void painoKeyCanclePress(int cancle) {
//        实现去掉按压效果
        allPianoKeys.get(cancle - 21).getKeyDrawable().setState(new int[]{-android.R.attr.state_pressed});
        // 将键重新进行绘制
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
