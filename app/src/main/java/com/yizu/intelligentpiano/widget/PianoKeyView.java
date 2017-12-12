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
 * ���ټ��̵Ĵ���
 */

public class PianoKeyView extends View {
    private static final String TAG = "PianoKeyView";
    //�׼�����Ŀ
    public final static int WHITE_PIANO_KEY_NUMS = 52;

    private Context mContext;
    //������ټ�
    private Piano mPiano;
    //���ֿ��
    private int mLayoutWidth;
    private int mLayoutHeight;

    //�ڰ׼��߶ȺͿ��
    private int mBlackKeyWidth;
    private int mBlackKeyHeight;
    private int mWhiteKeyWidth;
    private int mWhiteKeyHeight;

    private float scaleWidth;
    private float scaleHeight;

    //�׼����
    private ArrayList<PianoKey[]> whitePianoKeys;
    //�ڼ����
    private ArrayList<PianoKey[]> blackPianoKeys;
    //�ڰ׼�
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
        //��ȡ���ֿ��
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.white_piano_key);
        Drawable blackKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.black_piano_key);
        //�ڰ׼��Ŀ��
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int whiteKeyWidth = whiteKeyDrawable.getIntrinsicWidth();
        int blackKeyWidth = blackKeyDrawable.getIntrinsicWidth();
        int blackKeyHeight = blackKeyDrawable.getIntrinsicHeight();
        //���ݰ׼��Ŀ�߼������
        float scale = (float) whiteKeyHeight / whiteKeyWidth;
        MyLogUtils.e(TAG, "" + scale);
        //�׼����
        mWhiteKeyWidth = (mLayoutWidth + 60) / WHITE_PIANO_KEY_NUMS;
//        if (mLayoutWidth % WHITE_PIANO_KEY_NUMS != 0) {
//            mWhiteKeyWidth += 1;
//        }
//        ����ó����ֵ�ʵ�ʸ߶�
        mLayoutHeight = (int) ((mWhiteKeyWidth * scale * 10 + 9) / 10);
        mWhiteKeyHeight = mLayoutHeight;
        scaleHeight = (float) mWhiteKeyHeight / whiteKeyHeight;
        scaleWidth = (float) mWhiteKeyWidth / whiteKeyWidth;
        //����ڼ����
        mBlackKeyHeight = (int) (blackKeyHeight * scaleHeight);
        mBlackKeyWidth = (int) (blackKeyWidth * scaleWidth);
        //�������ò��ָ߶ȺͿ��
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
//            //��ȡ�׼�
            whitePianoKeys = mPiano.getWhitePianoKeys();
//            //��ȡ�ڼ�
            blackPianoKeys = mPiano.getBlackPianoKeys();
            //��ȡ���а�˳�����е����
            allPianoKeys = mPiano.getKeyList();
        }
//      ��ʼ���׼�
        for (int i = 0; i < whitePianoKeys.size(); i++) {
            for (PianoKey key : whitePianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
        //��ʼ���ڼ�
        for (int i = 0; i < blackPianoKeys.size(); i++) {
            for (PianoKey key : blackPianoKeys.get(i)) {
                key.getKeyDrawable().draw(canvas);
            }
        }
//        MyLogUtils.e(TAG, "���̻���ʱ�䣺" + (System.currentTimeMillis() - time));
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
     * ��ȡ�׼����
     *
     * @return
     */
    public int getmWhiteKeyWidth() {
        return mWhiteKeyWidth;
    }

    /**
     * ��ȡ�ڼ����
     *
     * @return
     */
    public int getmBlackKeyWidth() {
        return mBlackKeyWidth;
    }
}
