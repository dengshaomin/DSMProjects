package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/10/19.
 * All Rights Reserved by YiZu
 */

public class PullView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = "PullView";
    //白键宽度
    private int mWhiteKeyWidth;
    private int mBlackKeyWidth;


    private int mLayoutWith;
    private int mLayoutHeight;
    private Attributess mAttributess;

    private List<PullData> mData;

    private PianoKeyView mPianoKeyView;

    //每个duration多少像素
    private int mSpeedLenth = 0;
    //每个duration多少毫秒
    private int mSpeedTime = 0;
    private float mReta = 4;
    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 88;

    private Paint mPaint;
    //    private Paint mYellowPaint;
    private RectF mRectF;
    private PrgoressView mPrgoressView;
    private StaffView mStaffView;
    //第一条无线谱的每一小节的第一个音符
    List<Integer> fristSingLenth;

    SurfaceHolder holder;
    MysurfaceviewThread mysurfaceviewThread;
    private IPlayEnd iPlayEnd;

    //五线谱移动的距离
    private int staff = 0;
    //瀑布流向下移动的距离
    private int mScrollHeight = 0;
    //是否播放五线谱
    private boolean isPlay = false;
    //是否移动五线谱
    private boolean isMoveStaff = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    public PullView(Context context) {
        this(context, null);
    }

    public PullView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        holder = getHolder();//获取SurfaceHolder对象，同时指定callback
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

//        mYellowPaint = new Paint();
//        mYellowPaint.setStyle(Paint.Style.FILL);
//        mYellowPaint.setAntiAlias(true);
        mRectF = new RectF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWith = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }


    /**
     * 设置瀑布流数据
     *
     * @param mStaffView
     * @param mPianoKeyView
     * @param mProgessView
     */
    public void setPullData(StaffView mStaffView, PianoKeyView mPianoKeyView, PrgoressView mProgessView) {
        MyLogUtils.e(TAG, "setPullData");
        if (mPianoKeyView == null) return;
        mAttributess = null;
        mAttributess = mStaffView.getmAttributess();
        if (mAttributess == null) return;
        if (mStaffView.getPullData() == null) return;
        if (mProgessView == null) return;
        if (mStaffView.getFristSingLenth() == null) return;
        initAllData();
        this.mStaffView = mStaffView;
        this.mPrgoressView = mProgessView;
        this.mPianoKeyView = mPianoKeyView;
        fristSingLenth = mStaffView.getFristSingLenth();
        //每个duration多少像素
        mSpeedLenth = mStaffView.getmSpeedLenth();
        //每个duration多少毫秒
        mSpeedTime = mStaffView.getmSpeedTime();
        //默认每分钟88拍
        DEFAULT_TIME_NUM = mStaffView.getTimes();

        mData = mStaffView.getPullData();
        if (mWhiteKeyWidth == 0) {
            if (mPianoKeyView != null) {
                mWhiteKeyWidth = mPianoKeyView.getmWhiteKeyWidth();
                mBlackKeyWidth = mPianoKeyView.getmBlackKeyWidth();
            } else {
                return;
            }
        }
        caAllPosition();
    }

    /**
     * 计算所有位置
     */
    private void caAllPosition() {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            List<SaveTimeData> frist_hide = mData.get(i).getFrist();
            List<SaveTimeData> second_hide = mData.get(i).getSecond();
            for (int j = 0; j < frist_hide.size(); j++) {
                calculationPosiotion(frist_hide.get(j));
                frist_hide.get(j).setLastNode(false);
            }
            for (int j = 0; j < second_hide.size(); j++) {
                calculationPosiotion(second_hide.get(j));
                second_hide.get(j).setLastNode(false);
            }
        }
        float minY = 0;
        int minI = 0;
        boolean firstLine = false;
        int minJ = 0;
        for (int i = 0; i < size; i++) {
            List<SaveTimeData> frist_hide = mData.get(i).getFrist();
            List<SaveTimeData> second_hide = mData.get(i).getSecond();
            for (int j = 0; j < frist_hide.size(); j++) {
                if (frist_hide.get(j).getTop() < minY) {
                    minY = frist_hide.get(j).getTop();
                    minI = i;
                    minJ = j;
                    firstLine = true;
                }

            }
            for (int j = 0; j < second_hide.size(); j++) {
                if (second_hide.get(j).getTop() < minY) {
                    minY = second_hide.get(j).getTop();
                    minI = i;
                    minJ = j;
                    firstLine = false;
                }
            }
        }
        if (firstLine) {
            mData.get(minI).getFrist().get(minJ).setLastNode(true);
        } else {
            mData.get(minI).getSecond().get(minJ).setLastNode(true);
        }
    }

    /**
     * 开始播放
     */
    public void play() {
        if (!isPlay) {
            isPlay = true;
            if (mysurfaceviewThread == null) {
                mysurfaceviewThread = new MysurfaceviewThread();
                mysurfaceviewThread.start();
            }
            MyLogUtils.e(TAG, "开始播放");
        } else {
            if (mysurfaceviewThread != null) {
                mysurfaceviewThread.interrupt();
                mysurfaceviewThread = null;
            }
            isPlay = !isPlay;
            MyLogUtils.e(TAG, "暂停播放");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当SurfaceView被创建时，将画图Thread启动起来。
        if (mysurfaceviewThread != null) {
            isPlay = true;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当SurfaceView被销毁时，释放资源。
        synchronized (holder) {
            isPlay = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mysurfaceviewThread != null) {
            mysurfaceviewThread.interrupt();
            mysurfaceviewThread = null;
        }
        isPlay = !isPlay;
    }

    public void onResume() {
        int a = 1;
    }

    public void onPause() {
        isPlay = false;
    }

    public void setiPlayEnd(IPlayEnd iPlayEnd) {
        this.iPlayEnd = iPlayEnd;
    }

    /**
     * 内部类 MysurfaceviewThread,该类主要实现对canvas的具体操作。
     *
     * @author xu duzhou
     */
    class MysurfaceviewThread extends Thread {

        public MysurfaceviewThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            MyLogUtils.e(TAG, "线程开启");
            while (true) {
                if (isPlay) {
                    synchronized (holder) {
                        //锁定canvas
                        Canvas canvas = holder.lockCanvas();
                        //canvas 执行一系列画的动作
                        if (canvas != null) {
                            canvas.drawColor(Color.BLACK);
                            //canvas 执行一系列画的动作
                            int size = mData.size();

                            for (int i = 0; i < size; i++) {
                                List<SaveTimeData> frist_hide = mData.get(i).getFrist();
                                List<SaveTimeData> second_hide = mData.get(i).getSecond();
                                boolean lastNodeFlag = frist_hide.size() > second_hide.size();
                                for (int j = 0; j < frist_hide.size(); j++) {
                                    move(canvas, frist_hide.get(j), true, (i == size - 1 && lastNodeFlag) ? (j ==
                                            frist_hide.size() - 1 ? true : false) : false, i, j);

                                }
                                for (int j = 0; j < second_hide.size(); j++) {
                                    move(canvas, second_hide.get(j), false, (i == size - 1
                                            && !lastNodeFlag) ? (j == second_hide.size() - 1 ? true : false) : false, i, j);
                                }
                            }
                            try {
                                //释放canvas对象，并发送到SurfaceView
                                holder.unlockCanvasAndPost(canvas);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (isMoveStaff) {
                                staff += mSpeedLenth / mReta;
                                mStaffView.remove(staff);
                            }
                            try {
                                Thread.sleep((long) (mSpeedTime / mReta));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 计算每个音符的位置
     *
     * @param saveTimeData
     * @return
     */
    private void calculationPosiotion(SaveTimeData saveTimeData) {
        int octave = saveTimeData.getOctave();
        int black = saveTimeData.getBlackNum();
        int keyNum = 0;
        int key = (octave - 1) * 7;
        int num = mWhiteKeyWidth * key;
        key += 23;
        if (!saveTimeData.isRest()) {
            if (octave == 0) {
                switch (saveTimeData.getStep()) {
                    case "A":
                        if (black == 1) {
                            keyNum = 22;
                            mRectF.left = mWhiteKeyWidth - mBlackKeyWidth / 3;
                            mRectF.right = mWhiteKeyWidth + mBlackKeyWidth / 3;
                        } else {
                            keyNum = 21;
                            mRectF.left = 0;
                            mRectF.right = mWhiteKeyWidth - mBlackKeyWidth / 3;
                        }
                        break;
                    case "B":
                        if (black == -1) {
                            keyNum = 22;
                            mRectF.left = mWhiteKeyWidth - mBlackKeyWidth / 3;
                            mRectF.right = mWhiteKeyWidth + mBlackKeyWidth / 3;
                        } else {
                            keyNum = 23;
                            mRectF.left = mWhiteKeyWidth + mBlackKeyWidth / 3;
                            mRectF.right = mWhiteKeyWidth * 2;
                        }
                        break;
                }
            } else {
                switch (saveTimeData.getStep()) {
                    case "C":
                        if (black == 1) {
                            keyNum = key + 2;
                            mRectF.left = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 3 + num;
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
                            mRectF.right = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 3 + num;
                        } else if (black == -1) {
                            keyNum = key + 2;
                            mRectF.left = mWhiteKeyWidth * 3 - mWhiteKeyWidth / 3 + num;
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
                            mRectF.right = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 3 + num;
                        } else {
                            keyNum = key + 5;
                            mRectF.left = mWhiteKeyWidth * 4 + mWhiteKeyWidth / 3 + num;
                            mRectF.right = mWhiteKeyWidth * 5 + num;
                        }
                        break;
                    case "F":
                        if (black == 1) {
                            keyNum = 7;
                            mRectF.left = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 3 + num;
                            mRectF.right = mWhiteKeyWidth * 6 + mWhiteKeyWidth / 3 + num;
                        } else {
                            keyNum = 6;
                            mRectF.left = mWhiteKeyWidth * 5 + num;
                            mRectF.right = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 3 + num;
                        }
                        break;
                    case "G":
                        if (black == 1) {
                            keyNum = 9;
                            mRectF.left = mWhiteKeyWidth * 7 - mWhiteKeyWidth / 3 + num;
                            mRectF.right = mWhiteKeyWidth * 7 + mWhiteKeyWidth / 3 + num;
                        } else if (black == -1) {
                            keyNum = 7;
                            mRectF.left = mWhiteKeyWidth * 6 - mWhiteKeyWidth / 3 + num;
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
                            mRectF.right = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 3 + num;
                        } else {
                            keyNum = 12;
                            mRectF.left = mWhiteKeyWidth * 8 + mWhiteKeyWidth / 3 + num;
                            mRectF.right = mWhiteKeyWidth * 9 + num;
                        }
                        break;
                }
            }
        }
        saveTimeData.setPhysicalKey(keyNum);
        mRectF.top = mScrollHeight - (saveTimeData.getmAddDuration() + saveTimeData.getDuration()) * mSpeedLenth;
        mRectF.bottom = mScrollHeight - saveTimeData.getmAddDuration() * mSpeedLenth;
        saveTimeData.setTop(mRectF.top);
        saveTimeData.setBottom(mRectF.bottom);
        saveTimeData.setLeft(mRectF.left);
        saveTimeData.setRight(mRectF.right);
        saveTimeData.setArriveBottomState(0);
    }

    /**
     * 移动无线谱
     *
     * @param canvas
     * @param saveTimeData
     * @param firstLine
     * @param lastNode
     * @param i
     * @param j
     */
    private void move(Canvas canvas, final SaveTimeData saveTimeData, final boolean firstLine, boolean lastNode, final int i, final int j) {
        saveTimeData.setTop(saveTimeData.getTop() + mSpeedLenth / mReta);
        saveTimeData.setBottom(saveTimeData.getBottom() + mSpeedLenth / mReta);
        mRectF.left = saveTimeData.getLeft();
        mRectF.top = saveTimeData.getTop();
        mRectF.right = saveTimeData.getRight();
        mRectF.bottom = saveTimeData.getBottom();
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, getBottom());
        if (firstLine && saveTimeData.getArriveBottomState() == 1) {
            //该数据对应的音符第一次达到pullview底部
            if (j == 0) {
                if (i == 0) {
                    isMoveStaff = true;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPrgoressView.setIsShow(true);
                        }
                    });
                } else {
                    staff = fristSingLenth.get(i) - fristSingLenth.get(0);
                }
            }

        }
        if (mRectF.bottom > getTop() && mRectF.top < getBottom()) {
            if (!saveTimeData.isRest())
                canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, mPaint);
        }
        if (saveTimeData.isLastNode() && mRectF.top > getBottom()) {
            if (iPlayEnd != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        iPlayEnd.end();
                    }
                });
            }
            //初始化数据
            initAllData();
            caAllPosition();
            return;
        }
    }


    /**
     * 初始化所有数据
     */
    private void initAllData() {
        //五线谱移动的距离
        staff = 0;
        //瀑布流向下移动的距离
        mScrollHeight = 0;
        //是否播放五线谱
        isPlay = false;
        //是否移动五线谱
        isMoveStaff = false;
        mSpeedTime = mStaffView.getmSpeedTime();
        mStaffView.remove(0);
        handler.post(new Runnable() {
            @Override
            public void run() {
                //进度条屏蔽
                mPrgoressView.setIsShow(false);
            }
        });
        ScoreHelper.getInstance().initData();
//        if (mysurfaceviewThread != null) {
//            mysurfaceviewThread.interrupt();
//            mysurfaceviewThread = null;
//        }
    }

    /**
     * 加速
     */
    public void accelerate() {
        DEFAULT_TIME_NUM += 50;
        if (DEFAULT_TIME_NUM > 400) DEFAULT_TIME_NUM = 400;
        mSpeedTime = 60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions()));
        MyLogUtils.e(TAG, "加速：" + mSpeedTime);
    }

    /**
     * 减速
     */
    public void deceleration() {
        DEFAULT_TIME_NUM -= 50;
        if (DEFAULT_TIME_NUM < 30) DEFAULT_TIME_NUM = 30;
        mSpeedTime = 60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions()));
        MyLogUtils.e(TAG, "减速：" + mSpeedTime);
    }

    /**
     * 是否显示画笔
     *
     * @param isShowPull
     */
    public void isShow(boolean isShowPull) {
        if (!isShowPull) {
            mPaint.setColor(getResources().getColor(R.color.none));
        } else {
            mPaint.setColor(getResources().getColor(R.color.blue));
        }
    }
}
