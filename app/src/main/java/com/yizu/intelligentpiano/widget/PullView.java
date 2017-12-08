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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLCanvas;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLContinuousView;
import com.chillingvan.canvasgl.shapeFilter.BasicDrawShapeFilter;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.PullBack;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaozhu on 2017/10/19.
 * All Rights Reserved by YiZu
 */

public class PullView extends GLContinuousView {

    private final static String TAG = "PullView";

    //白键宽度
    private int mWhiteKeyWidth;

    private int mBlackKeyWidth;


    private int mLayoutWith;

    private int mLayoutHeight;

    private Attributess mAttributess;

    private List<PullData> mData;

    //    //每个duration多少像素
    private float mSpeedLenth = 0;

    private float mReta = 0.8f;

    //默认显示瀑布流
    private boolean isShowPullView = true;

    private Paint mYellowPaint;

    private GLPaint mBackgroundPaint;

    private RectF mRectF;

    private StaffView mStaffView;

    //第一条无线谱的每一小节的第一个音符
    List<Float> fristSingLenth;

    SurfaceHolder holder;


    private IPlayEnd iPlayEnd;

    //五线谱移动的距离
    private float staff = 0;

    //是否播放五线谱
    private boolean isPlay = false;

    //是否移动五线谱
    private boolean isMoveStaff = false;


    //用来保存瀑布流灰色背景
    private List<PullBack> mBackList = new ArrayList<>();

    //用来保存光标的位置
    private float centerX = 0;

    //瀑布流从第几小节开始
    private int index = 0;

    //瀑布流下落得距离
    private float move = 0;

    //用来保存开始计算的时间（毫秒数）
    long time = 0;

    //缩小时间的误差
    float timeError = 0;

    private GLPaint mPaint;

    private Handler handler = new Handler(Looper.getMainLooper());

    /************只管时间不管速度(200拍的速度是最快的，减小速度只需要缩短每次移动的长度)**************/
    private int mTimess = 60;

    private float mLenth;//100拍的长度

    public PullView(Context context) {
        this(context, null);
    }

    public PullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();//获取SurfaceHolder对象，同时指定callback
        holder.addCallback(this);

//        //整个界面透明
//        holder.setFormat(PixelFormat.TRANSPARENT);
//        setZOrderOnTop(true);

        mYellowPaint = new Paint();
        mYellowPaint.setStyle(Paint.Style.FILL);
        mYellowPaint.setColor(getResources().getColor(R.color.yellow));
        mYellowPaint.setAntiAlias(true);
        mRectF = new RectF();

        mPaint = new GLPaint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.blue));
//        mPaint.setAntiAlias(true);

        mBackgroundPaint = new GLPaint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(getResources().getColor(R.color.pullcolor));
//        mBackgroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        if (isPlay) {
            long time = System.currentTimeMillis();
            move += mLenth * mReta;
            //显示瀑布流就绘制
            if (isShowPullView) {
                int size = Math.min(mData.size(), (index + 3));
                mBackList.clear();
                if (move == 0) {
                    ScoreHelper.getInstance().initData();//初始化打分
                }
                for (int i = index; i < size; i++) {
                    List<SaveTimeData> frist_hide = mData.get(i).getFrist();
                    for (int j = 0; j < frist_hide.size(); j++) {
                        move(canvas, frist_hide.get(j), i, j, true);
                    }
                }
                for (int i = index; i < size; i++) {
                    List<SaveTimeData> second_hide = mData.get(i).getSecond();
                    for (int j = 0; j < second_hide.size(); j++) {
                        move(canvas, second_hide.get(j), i, j, false);
                    }
                }
//                for (int k = 0; k < mBackList.size(); k++) {
//                    //引导条
//                    mRectF.left = mBackList.get(k).getLeft();
//                    mRectF.top = 0;
//                    mRectF.right = mBackList.get(k).getRight();
//                    mRectF.bottom = mLayoutHeight;
//                    mCanvas.drawRect(mRectF, mBackgroundPaint);
//                }
            }
            //驱动五线谱
            if (isMoveStaff) {
                staff += mLenth * mReta;
                int center = mLayoutWith / 2 - (mLayoutWith - mStaffView.getmLayoutWidth());
                if (staff < center) {
                    centerX = staff;
                }
                mStaffView.remove(staff - centerX, index, centerX);
            }
            try {
                Log.e("code",(System.currentTimeMillis() - time) + "");
                Thread.sleep((long) Math.max(0f, 16 - (System.currentTimeMillis() - time)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWith = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }


    /**
     * 设置瀑布流数据
     */
    public void setPullData(StaffView mStaffView, KeyView mPianoKeyView,
            final IPlay iPlay) {
        MyLogUtils.e(TAG, "初始化第一条瀑布流");
        if (mPianoKeyView == null) {
            return;
        }
        if (mStaffView == null) {
            return;
        }
        this.mStaffView = mStaffView;
        mAttributess = null;
        mAttributess = mStaffView.getmAttributess();
        if (mAttributess == null) {
            return;
        }
        if (mStaffView.getPullData() == null) {
            return;
        }

        if (mStaffView.getFristSingLenth() == null) {
            return;
        }
        initAllData();
        fristSingLenth = mStaffView.getFristSingLenth();
//        //每个duration多少像素
        mSpeedLenth = mStaffView.getmSpeedLenth();
//        //每个duration多少毫秒
//        mSpeedTime = mStaffView.getmSpeedTime();
//        mTimesTime = 60 * 1000 / mStaffView.getTimes();
        mLenth = mSpeedLenth * Float.valueOf(mAttributess.getDivisions()) / 20;
//        mLenth = 5;
        //默认每分钟88拍
//        DEFAULT_TIME_NUM = mStaffView.getTimes();
//        MyLogUtils.e(TAG, "拍数：" + DEFAULT_TIME_NUM);

        mData = mStaffView.getPullData();
        if (mData == null) {
            return;
        }
        if (mWhiteKeyWidth == 0) {
            mWhiteKeyWidth = mPianoKeyView.getmWhiteKeyWidth();
            mBlackKeyWidth = mPianoKeyView.getmBlackKeyWidth();
        }
        caAllPosition(true);
        handler.post(new Runnable() {
            @Override
            public void run() {
                iPlay.ReadyFinish();
            }
        });
    }

    /**
     * 计算所有位置
     */
    private void caAllPosition(boolean isFrist) {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            List<SaveTimeData> frist_hide = mData.get(i).getFrist();
            List<SaveTimeData> second_hide = mData.get(i).getSecond();
            for (int j = 0; j < frist_hide.size(); j++) {
                calculationPosiotion(frist_hide.get(j), isFrist);
                frist_hide.get(j).setLastNode(false);
            }
            for (int j = 0; j < second_hide.size(); j++) {
                calculationPosiotion(second_hide.get(j), isFrist);
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
     * 播放/暂停
     */
    public void play(boolean isplay) {
        if (mStaffView == null) {
            MyToast.ShowLong("五线谱初始化失败");
            return;
        }
        isPlay = isplay;
        if (isplay) {
            invalidate();
        }
//        if (thread != null) {
//            thread.interrupt();
//            thread = null;
//        }
//        if (isPlay) {
//            thread = new MysurfaceviewThread();
//            thread.start();
//        }
    }

    public void resetPullView() {
        isPlay = false;
        staff = 0;
        centerX = 0;
        index = 0;
        move = 0;
        isMoveStaff = false;
        endRefreshCanvas();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onResume() {
//        isPlay = true;
    }

    public void onPause() {
        isPlay = false;
    }

    public void setiPlayEnd(IPlayEnd iPlayEnd) {
        this.iPlayEnd = iPlayEnd;
    }


    /**
     * 计算每个音符的位置
     */
    private void calculationPosiotion(SaveTimeData saveTimeData, boolean isFrist) {
        if (isFrist) {
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
            mRectF.top = 0 - (saveTimeData.getmAddDuration() + saveTimeData.getDuration() + (saveTimeData.isTie() ? 1 : 0)) * mSpeedLenth;
            mRectF.bottom = 0 - saveTimeData.getmAddDuration() * mSpeedLenth;
            saveTimeData.setTop(mRectF.top);
            saveTimeData.setBottom(mRectF.bottom);
            saveTimeData.setLeft(mRectF.left - 28);
            saveTimeData.setRight(mRectF.right - 30);
        }
        saveTimeData.setArriveBottomState(0);
    }

    /**
     * 移动无线谱
     */
    private void move(ICanvasGL canvas, final SaveTimeData saveTimeData, final int i, final int j, boolean isFrist) {
        mRectF.left = saveTimeData.getLeft();
        mRectF.top = saveTimeData.getTop() + move;
        mRectF.right = saveTimeData.getRight();
        mRectF.bottom = saveTimeData.getBottom() + move;
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, mLayoutHeight);
        if (isFrist && saveTimeData.getArriveBottomState() == 1) {
            //该数据对应的音符第一次达到pullview底部
            if (j == 0) {
                if (i == 0) {
                    isMoveStaff = true;
                }
                staff = fristSingLenth.get(i);
                index = i;
            }
        }
        if (mRectF.bottom > 0 && mRectF.top < mLayoutHeight) {
            if (!saveTimeData.isRest()) {
                canvas.drawRect(mRectF, mPaint);
                //保存引导条
                boolean isSave = false;
                for (int k = 0; k < mBackList.size(); k++) {
                    if (mBackList.get(k).getLeft() == mRectF.left) {
                        isSave = true;
                        return;
                    }
                }
                if (!isSave) {
                    mBackList.add(new PullBack(mRectF.left, mRectF.right));
                }
            }
        }

//        绘制结束
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
            caAllPosition(false);
            return;
        }
    }


    /**
     * 初始化所有数据
     */
    private void initAllData() {
        //五线谱移动的距离
        staff = 0;
        centerX = staff;
        //是否播放五线谱
        isPlay = false;
        //是否移动五线谱
        isMoveStaff = false;
//        mSpeedTime = mStaffView.getmSpeedTime();
//        mTimesTime = 60 * 1000 / mStaffView.getTimes();
        index = 0;
        move = 0;
        timeError = 0;
        mStaffView.remove(0, index, centerX);
    }

    /**
     * 绘制结束刷新画布
     */
    private void endRefreshCanvas() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                SurfaceHolder surfaceHolder = holder;
//                synchronized (surfaceHolder) {
//                    //锁定canvas
//                    try {
//                        Canvas canvas = surfaceHolder.lockCanvas();
//                        //canvas 执行一系列画的动作
//                        if (canvas != null) {
//                            canvas.drawColor(Color.BLACK);
//                            surfaceHolder.unlockCanvasAndPost(canvas);
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            }
//        }).start();
    }


    /**
     * 加速
     */
    public String accelerate() {
        mReta += 0.1f;
        if (mReta >= 1.5f) {
            mReta = 1.5f;
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }

    /**
     * 减速
     */
    public String deceleration() {
        mReta -= 0.1f;
        if (mReta <= 0.5f) {
            mReta = 0.5f;
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }

    /**
     * 是否显示画笔
     */
    public void isShow(boolean isShowPull) {
        isShowPullView = isShowPull;
    }

    public void onDrestry() {
    }


    public String getmReta() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }
}
