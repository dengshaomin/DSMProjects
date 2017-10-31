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
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
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

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x1233) {
                mScrollHeight += mSpeedLenth / 4;
//                if (mScrollHeight>)
                invalidate();
//                scrollTo(0, -mScrollHeight);
                mTimer.schedule(new MyTimerTask(), mSpeedTime / 4);
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

        mSpeedTime = 60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions()));
        mTimeError = mLayoutHeight - mSpeedLenth * num;
        MyLogUtils.e(TAG, "mSpeedTime：" + mSpeedTime);
        MyLogUtils.e(TAG, "mLayoutHeight：" + mLayoutHeight);
        MyLogUtils.e(TAG, "mSpeedLenth：" + mSpeedLenth);
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
        MyLogUtils.e(TAG, "onDraw");
        MyLogUtils.e(TAG, "mTimeError" + mTimeError);
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
        if (octave == 0) {
            switch (saveTimeData.getStep()) {
                case "A":
                    mRectF.left = 0;
                    mRectF.right = mWhiteKeyWidth - mBlackKeyWidth / 2;
                    break;
                case "B":
                    mRectF.left = mWhiteKeyWidth + mBlackKeyWidth / 2;
                    mRectF.right = mWhiteKeyWidth * 2;
                    break;
            }
        } else {
            switch (saveTimeData.getStep()) {
                case "C":
                    mRectF.left = mWhiteKeyWidth * 2 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 2 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "D":
                    mRectF.left = mWhiteKeyWidth * 3 + mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 4 - mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "E":
                    mRectF.left = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 2 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 5 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "F":
                    mRectF.left = mWhiteKeyWidth * 5 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 2 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "G":
                    mRectF.left = mWhiteKeyWidth * 6 + mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 7 - mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "A":
                    mRectF.left = mWhiteKeyWidth * 7 + mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 8 - mWhiteKeyWidth / 3 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
                case "B":
                    mRectF.left = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 2 + mWhiteKeyWidth * (octave - 1) * 7;
                    mRectF.right = mWhiteKeyWidth * 9 + mWhiteKeyWidth * (octave - 1) * 7;
                    break;
            }
        }
        mRectF.top = mScrollHeight + mTimeError - (saveTimeData.getmAddDuration() + saveTimeData.getDuration()) * mSpeedLenth;
        mRectF.bottom = mScrollHeight + mTimeError - saveTimeData.getmAddDuration() * mSpeedLenth;
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, getBottom());
        if (mRectF.bottom > getTop() && mRectF.top < getBottom()) {
            canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, mPaint);
        }
        return 0;
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
