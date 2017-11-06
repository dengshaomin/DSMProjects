package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.utils.MyLogUtils;

/**
 * Created by liuxiaozhu on 2017/10/23.
 * All Rights Reserved by YiZu
 */

public class PrgoressView extends View {
    private final static String TAG = "PrgoressView";

    //蓝色进度条矩形
    private RectF mProgessRectF;
    //绘制蓝色进度条的画笔
    private Paint mBluePaint;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private StaffView mStaffView;

    private int mLinsRoomWidth;
    private int twoStaff_fristLins_up;
    private boolean isTowStaff;
    private boolean isShow = false;

    public PrgoressView(Context context) {
        this(context, null);
    }

    public PrgoressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrgoressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mBluePaint = new Paint();
        mBluePaint.setAntiAlias(true);
        mBluePaint.setColor(getResources().getColor(R.color.violet));
        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setAlpha(70);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        MyLogUtils.e(TAG, "onDraw");

        if (!isShow) return;
        if (mStaffView != null && mStaffView.getFristSingLenth().size() > 1) {
            setBlueProgresBar(canvas, mStaffView.getFristSingLenth().get(0));
        }
    }

    /**
     * 设置蓝色进度条
     *
     * @param canvas
     */
    private void setBlueProgresBar(Canvas canvas, float mLeft) {
        if (mProgessRectF == null) {
            mProgessRectF = new RectF();
        }

        mProgessRectF.left = mLeft;
        mProgessRectF.top = 10;
//        mProgessRectF.right = mLeft + mLinsRoomWidth * 3;
        mProgessRectF.right = mLeft + 5;
        if (isTowStaff) {
            mProgessRectF.bottom = mLayoutHeight - 10;
            canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
        } else {
            mProgessRectF.bottom = twoStaff_fristLins_up + mLinsRoomWidth * 4;
        }
        canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
    }

    public void setPrgoressData(StaffView staffView) {
        isShow = false;

        if (staffView == null) return;
        mStaffView = staffView;
        mLinsRoomWidth = mStaffView.getmLinsRoomWidth();
        twoStaff_fristLins_up = mStaffView.getTwoStaff_fristLins_up();
        isTowStaff = mStaffView.isTowStaff();
        isShow = false;
        invalidate();
    }

    /**
     * 设置是否显示进度条
     *
     * @param show
     */
    public void setIsShow(boolean show) {
        isShow = show;
        invalidate();
    }
}
