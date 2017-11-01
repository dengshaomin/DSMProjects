package com.yizu.intelligentpiano.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.constens.IPlayState;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuxiaozhu on 2017/10/19.
 * All Rights Reserved by YiZu
 */

public class PullView extends View {
    private final static String TAG = "PullView";

    //白键总数目
    private final static int WHITE_PIANO_KEY_NUMS = 52;
    private int mWhiteKeyWidth;
    private int mBlackKeyWidth;


    private int mLayoutWith;
    private int mLayoutHeight;
    private boolean isShow = true;
    private Attributess mAttributess;

    private List<PullData> mData;

    private PianoKeyView mPianoKeyView;

    //每个duration多少像素
    private int mSpeedLenth = 0;
    //每个duration多少毫秒
    private int mSpeedTime = 0;
    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 88;

    private Paint mPaint;
    private RectF mRectF;

    private Timer mTimer;

    private int mScrollHeight = 0;
    int num;

    private int mTimeError = -1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x1233) {
                mScrollHeight += mSpeedLenth / 10;
                mTimer.schedule(new MyTimerTask(), mSpeedTime / 10);
                invalidate();
//                scrollTo(0, -mScrollHeight);
            }
        }
    };


    public PullView(Context context) {
        this(context, null);
    }

    public PullView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.blue));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWith = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        MyLogUtils.e(TAG, "mLayoutHeight：" + mLayoutHeight);
        if (mAttributess == null) {
            return;
        }
        //一个小结的duration总数量
        num = Integer.valueOf(mAttributess.getDivisions()) * Integer.valueOf(mAttributess.getTime().getBeats());
//        计算每个duration的距离
        mSpeedLenth = mLayoutHeight / num;

        mSpeedTime = (60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions())));
        mTimeError = mLayoutHeight - mSpeedLenth * num;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWhiteKeyWidth == 0) {
            if (mPianoKeyView != null) {
                mWhiteKeyWidth = mPianoKeyView.getmWhiteKeyWidth();
                mBlackKeyWidth = mPianoKeyView.getmBlackKeyWidth();
            } else {
                return;
            }
        }
        if (mData == null) {
            return;
        }

        if (isShow) {
            int size = mData.size();
            for (int i = 0; i < size; i++) {
                List<SaveTimeData> frist_hide = mData.get(i).getFrist();
                List<SaveTimeData> second_hide = mData.get(i).getSecond();
                for (int j = 0; j < frist_hide.size(); j++) {
                    calculationPosiotion(canvas, frist_hide.get(j), i, j);
                }
                for (int j = 0; j < second_hide.size(); j++) {
                    calculationPosiotion(canvas, second_hide.get(j), i, j);
                }
            }
        }
    }

    /**
     * 设置瀑布流数据
     *
     * @param mStaffView
     * @param mPianoKeyView
     */
    public void setPullData(StaffView mStaffView, PianoKeyView mPianoKeyView) {
        MyLogUtils.e(TAG, "setPullData");
        if (mPianoKeyView == null) {
            return;
        }
        Attributess attributess = mStaffView.getmAttributess();
        if (attributess == null) {
            MyLogUtils.e(TAG, "attributess为空");
            return;
        }
        if (mStaffView.getPullData() == null) {
            MyLogUtils.e(TAG, "data为空");
            return;
        }

        this.mPianoKeyView = mPianoKeyView;
        mAttributess = attributess;
        mData = mStaffView.getPullData();
        ScoreHelper.getInstance().setTotalNode(mData.size());
        invalidate();
    }


    /**
     * 是否显示(默认显示)
     *
     * @param show
     */
    public void isShow(boolean show) {
        if (show) {
            isShow = show;
            invalidate();
        }
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        if (isShow) {
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(new MyTimerTask(), mSpeedTime / 4);
        }
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        if (isShow && mTimer != null) {
//            if (progressView != null) progressView.stopPlay();
            mTimer.cancel();
            mTimer = new Timer();
        }
    }

    /**
     * 计算每个音符的位置
     *
     * @param canvas
     * @param saveTimeData
     * @return
     */
    private float calculationPosiotion(Canvas canvas, SaveTimeData saveTimeData, int i, int j) {
        int octave = saveTimeData.getOctave();
        int black = saveTimeData.getBlackNum();
        int keyNum = 0;
        int key = (octave - 1) * 7;
        int num = mWhiteKeyWidth * key;
        key += 23;
        if (octave == 0) {
            switch (saveTimeData.getStep()) {
                case "A":
                    if (black == 1) {
                        keyNum = 22;
                        mRectF.left = mWhiteKeyWidth - mBlackKeyWidth / 2;
                        mRectF.right = mWhiteKeyWidth + mBlackKeyWidth / 2;
                    } else {
                        keyNum = 21;
                        mRectF.left = 0;
                        mRectF.right = mWhiteKeyWidth - mBlackKeyWidth / 2;
                    }
                    break;
                case "B":
                    if (black == -1) {
                        keyNum = 22;
                        mRectF.left = mWhiteKeyWidth - mBlackKeyWidth / 2;
                        mRectF.right = mWhiteKeyWidth + mBlackKeyWidth / 2;
                    } else {
                        keyNum = 23;
                        mRectF.left = mWhiteKeyWidth + mBlackKeyWidth / 2;
                        mRectF.right = mWhiteKeyWidth * 2;
                    }
                    break;
            }
        } else {
            switch (saveTimeData.getStep()) {
                case "C":
                    if (black == 1) {
                        keyNum = key + 2;
                        mRectF.left = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 3 + mWhiteKeyWidth / 3 + num;
                    } else {
                        keyNum = key + 1;
                        mRectF.left = mWhiteKeyWidth * 2 + num;
                        mRectF.right = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 2 + num;
                    }
                    break;
                case "D":
                    if (black == 1) {
                        keyNum = key + 4;
                        mRectF.left = mWhiteKeyWidth * 4 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 2 + num;
                    } else if (black == -1) {
                        keyNum = key + 2;
                        mRectF.left = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 3 + mWhiteKeyWidth / 3 + num;
                    } else {
                        keyNum = key + 3;
                        mRectF.left = mWhiteKeyWidth * 3 + mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 4 - mWhiteKeyWidth / 3 + num;
                    }
                    break;
                case "E":
                    if (black == -1) {
                        keyNum = key + 4;
                        mRectF.left = mWhiteKeyWidth * 4 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 2 + num;
                    } else {
                        keyNum = key + 5;
                        mRectF.left = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 5 + num;
                    }
                    break;
                case "F":
                    if (black == 1) {
                        keyNum = 7;
                        mRectF.left = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 6 + mWhiteKeyWidth / 3 + num;
                    } else {
                        keyNum = 6;
                        mRectF.left = mWhiteKeyWidth * 5 + num;
                        mRectF.right = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 2 + num;
                    }
                    break;
                case "G":
                    if (black == 1) {
                        keyNum = 9;
                        mRectF.left = mWhiteKeyWidth * 7 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 7 + mWhiteKeyWidth / 3 + num;
                    } else if (black == -1) {
                        keyNum = 7;
                        mRectF.left = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 6 + mWhiteKeyWidth / 3 + num;
                    } else {
                        keyNum = 8;
                        mRectF.left = mWhiteKeyWidth * 6 + mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 7 - mWhiteKeyWidth / 3 + num;
                    }
                    break;
                case "A":
                    if (black == 1) {
                        keyNum = 11;
                        mRectF.left = mWhiteKeyWidth * 8 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 2 + num;
                    } else if (black == -1) {
                        keyNum = 9;
                        mRectF.left = mWhiteKeyWidth * 7 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 7 + mWhiteKeyWidth / 3 + num;
                    } else {
                        keyNum = 10;
                        mRectF.left = mWhiteKeyWidth * 7 + mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 8 - mWhiteKeyWidth / 3 + num;
                    }
                    break;
                case "B":
                    if (black == -1) {
                        keyNum = 11;
                        mRectF.left = mWhiteKeyWidth * 8 - mWhiteKeyWidth / 3 + num;
                        mRectF.right = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 2 + num;
                    } else {
                        keyNum = 12;
                        mRectF.left = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 2 + num;
                        mRectF.right = mWhiteKeyWidth * 9 + num;
                    }
                    break;
            }
        }
        mRectF.top = mScrollHeight + mTimeError - (saveTimeData.getmAddDuration() + saveTimeData.getDuration()) * mSpeedLenth;
        mRectF.bottom = mScrollHeight + mTimeError - saveTimeData.getmAddDuration() * mSpeedLenth;
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, getBottom());
//        if (mRectF.bottom >= getBottom() && mRectF.top <= getBottom()) {
//            if (!isProgressViewStart) {
//                isProgressViewStart = true;
//                progressView.startPlay();
//            }
//        }
        if (mRectF.bottom > getTop() && mRectF.top < getBottom()) {
            canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, mPaint);
        }
        return 0;
    }

    private PrgoressView progressView;

    public void setProgressView(PrgoressView progressView) {
//        this.progressView = progressView;
//        progressView.setiPlayState(new IPlayState() {
//            @Override
//            public void start() {
//                if (iPlayState != null) {
//                    iPlayState.start();
//                }
//            }
//
//            @Override
//            public void end() {
//                stopPlay();
//                mScrollHeight = 0;
//                isProgressViewStart = false;
//                if (iPlayState != null) {
//                    iPlayState.end();
//                }
//            }
//        });
    }

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
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(0x1233);
        }
    }

}
