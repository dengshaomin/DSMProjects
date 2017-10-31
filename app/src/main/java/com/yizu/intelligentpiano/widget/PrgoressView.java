package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Move;
import com.yizu.intelligentpiano.constens.IPlayState;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private int measureDurationNum;
    private int mSpeedTime;

    //    //第一条五线谱一个音符移动的距离和duration（进度条）
//    private List<Move> mMoveLantehList_Frist;
//    //第二条五线谱一个音符移动的距离 （进度条）
//    private List<Move> mMoveLantehList_second;
    private List<Move> mData;
    private int mLinsRoomWidth;
    private int twoStaff_fristLins_up;
    private boolean isTowStaff;
    private int mMovePosiotion = -1;
    private int mLenth = 0;
    private int padding = 0;
    private IPlayState iPlayState;

    public IPlayState getiPlayState() {
        return iPlayState;
    }

    public void setiPlayState(IPlayState iPlayState) {
        this.iPlayState = iPlayState;
    }

    /**
     * 定时器
     */
    private Timer mTimer;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mMovePosiotion++;
            if (msg.what == 0x1231) {
                if (mMovePosiotion == mData.size()) {
                    mMovePosiotion = -1;
                    padding = 0;
                    mStaffView.scrollTo(0, 0);
                    mLenth = 0;
                    if (iPlayState != null) {
                        iPlayState.end();
                    }
                    invalidate();
                } else {
                    if(mMovePosiotion > mData.size() -1) return;
                    if (iPlayState != null) {
                        iPlayState.start();
                    }
                    if (mData.get(mMovePosiotion).getLenth() / (mLayoutWidth - 100) == padding) {
                    } else {
                        mLenth = mData.get(mMovePosiotion).getLenth() - mLinsRoomWidth * 2;
                        padding++;
                        mStaffView.scrollTo((int) mLenth, 0);
                    }
                    mTimer.schedule(new MyTimerTask(), mSpeedTime * mData.get(mMovePosiotion).getDuration());
                    invalidate();
                }
            }
        }
    };

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
        if (mMovePosiotion == -1) {
            return;
        }
        if (mStaffView != null && mData != null) {
            setBlueProgresBar(canvas, mData.get(mMovePosiotion).getLenth() - mLenth);
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
        mProgessRectF.right = mLeft + mLinsRoomWidth * 3;
        if (isTowStaff) {
            mProgessRectF.bottom = mLayoutHeight - 10;
            canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
        } else {
            mProgessRectF.bottom = twoStaff_fristLins_up + mLinsRoomWidth * 4;
        }
        canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
    }


    /**
     * 定时器
     */
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(0x1231);
        }
    }


    /**
     * 开始滚动
     */
    public void startPlay() {
        mMovePosiotion = -1;
        MyLogUtils.e(TAG, "开始播放");
        if (mTimer == null) {
            mTimer = new Timer();
        }
        //开始播放，延时一小节的时间
        mTimer.schedule(new MyTimerTask(), measureDurationNum * mSpeedTime);
    }

    /**
     * 结束滚动
     */
    public void stopPlay() {
        if (mTimer != null) {
            MyLogUtils.e(TAG, "停止播放");
            mTimer.cancel();
            mTimer = new Timer();
        }
    }

    public void setPrgoressData(StaffView staffView) {
        if (staffView == null) return;
        mStaffView = staffView;
        measureDurationNum = mStaffView.getMeasureDurationNum();
        mSpeedTime = mStaffView.getmSpeedTime();
        if (mStaffView.getmMoveLantehList_Frist().size() > mStaffView.getmMoveLantehList_second().size()) {
            mData = mStaffView.getmMoveLantehList_Frist();
        } else {
            mData = mStaffView.getmMoveLantehList_second();
        }
//        mMoveLantehList_Frist = mStaffView.getmMoveLantehList_Frist();
//        mMoveLantehList_second = mStaffView.getmMoveLantehList_second();
        mLinsRoomWidth = mStaffView.getmLinsRoomWidth();
        twoStaff_fristLins_up = mStaffView.getTwoStaff_fristLins_up();
        isTowStaff = mStaffView.isTowStaff();
        ScoreHelper.getInstance().setTotalNode(mData.size());
        invalidate();
    }
}
