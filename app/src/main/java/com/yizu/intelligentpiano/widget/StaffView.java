package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Dot;
import com.yizu.intelligentpiano.bean.Dwon;
import com.yizu.intelligentpiano.bean.HeadData;
import com.yizu.intelligentpiano.bean.Legato;
import com.yizu.intelligentpiano.bean.Rest;
import com.yizu.intelligentpiano.bean.StaffJump;
import com.yizu.intelligentpiano.bean.Tia;
import com.yizu.intelligentpiano.bean.Tial;
import com.yizu.intelligentpiano.bean.Tie;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.AttributessTime;
import com.yizu.intelligentpiano.bean.xml.Beam;
import com.yizu.intelligentpiano.bean.xml.Clef;
import com.yizu.intelligentpiano.bean.xml.Measure;
import com.yizu.intelligentpiano.bean.xml.MeasureBase;
import com.yizu.intelligentpiano.bean.xml.Notes;
import com.yizu.intelligentpiano.bean.StaffSaveData;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by liuxiaozhu on 2017/10/11.
 * All Rights Reserved by YiZu
 * 五线谱
 */

public class StaffView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "StaffView";
    //乐谱线的基本宽度
    private static final int STAFF_LINS_WSITH = 1;

    private Context mContext;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mLayoutCenterHeight;
    //true：二条五线谱上
    private boolean isTowStaff = false;
    //是否绘制符尾（八分音符以后的倾斜符尾）
    private boolean isDrawTial = true;

    //存储五线谱的数据
    //第一条线的数据
    private List<Measure> mFristStaffData;
    //第二条线的数据
    private List<Measure> mSecondStaffData;
    private List<Integer> mBackUPData;

    private Attributess mAttributess;


    /**
     * 画笔
     */
    //绘制黑色符头的画笔
    private Paint mBlackPaint;
    //绘制白色符头的画笔
    private Paint mWhitePaint;
    //绘制符尾
    private Paint mTailPaint;
    //绘制主要基线
    private Paint mLinsPaint;
    private Paint mCrudePaint;
    private Paint mRedPaint;
    private Paint mBeamPaint;

    private Paint mLinsPaint2;
    //进度条矩形
    private RectF mProgessRectF;
    //绘制进度条的画笔
    private Paint mBluePaint;
    /**
     * 一个间间距
     */
    private int mLinsRoomWidth = 0;
    private int mLinsRoomWidth2;
    private int mLinsRoomWidth3;
    private int mLinsRoomWidth4;
    private int mLinsRoomWidth5;
    private int mLinsRoomWidth6;
    private int mLinsRoomWidth7;
    private int mLinsRoomWidth8;

    //第一条五线谱总的宽度
    private float mFristStaffWidth;
    private float mScendStaffWidth;
    /**
     * 音符缩放比
     */
    private float mTrebleScale;
    private float mBassScale;
    private float mNumScale;
    private float mTwelveScale;
    /**
     * 音符缩放之后的宽高
     */
    private int mTrebleWidth;
    private int mBassWidth;
    private int mNumWidth;
    private int mTwelveWidth;
    private int mTrebleHeight;
    private int mBassHeight;
    private int mNumHeight;
    private int mTwelveHeight;
    /**
     * 音符Drawable
     */
    //高音
    private Drawable trebleDrawable;
    //低音
    private Drawable bassDrawable;
    //2，3，4，6，8，9，12
    private Drawable twoDrawable;
    private Drawable threeDrawable;
    private Drawable fourDrawable;
    private Drawable sixDrawable;
    private Drawable eightDrawable;
    private Drawable nineDrawable;
    private Drawable twelveDrawable;


    /**
     * 常用参数
     */
    //两条五线谱
    private int twoStaff_fristLins_up;
    private int twoStaff_fiveLins_up;
    private int twoStaff_threeLins_up;
    private int twoStaff_fristLins_down;
    private int twoStaff_fiveLins_down;
    private int twoStaff_threeLins_down;


    /**
     * 钢琴移动相关
     */


    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 80;

    //连音（处理）
    private List<Tie> mTie = new ArrayList<>();
    private List<Tie> mSlur = new ArrayList<>();

    //保存第一个音符所有短连音符
    private List<Legato> forwardHook = new ArrayList<>();
    private Map<Integer, List<Legato>> legatosMap = new HashMap<>();


    //每小节多少Duration
    private int measureDurationNum = 0;
    //默认每个druction的长度20像素
    private int mSpeedLenth = 20;
    //每个duration多少毫秒
    private float mSpeedTime = 0;
    //每一小节第一条五线谱的第一个音符的长度
    private List<Float> fristSingLenth = new ArrayList<>();

    private Path mPath;

    SurfaceHolder holder;
    MysurfaceviewThread thread;
    private boolean isUpfifth = false;
    //保存整个谱子升降音的数组
    private String[] fifth;
    //是否保存五线谱移动的数据
    private boolean isSaveData = false;
    private boolean isMove = false;
    private float moveLenth;

    private List<Integer> HighRise;//高升
    private List<Integer> HighDrop;//高降
    private List<Integer> LowRise;//低升
    private List<Integer> LowDrop;//低降

    private Double[] typeNum = {1.0, 1.0 / 2, 1.0 / 4, 1.0 / 8, 1.0 / 16, 1.0 / 32, 1.0 / 64};
    private String[] typeString = {"whole", "half", "quarter", "eighth", "16th", "32th", "64th"};
    int divisions = 0;
    int beats = 0;
    //    int startIndex = 0;
    private Canvas mCanvas;
//    private boolean isSteam = false;

//    private int index = 0;

    private float mLenth;//1拍的长度
    private float mReta = 0.8f;//80拍


    /**
     * 保存全部音符的位置信息
     */
    //间线
    private List<StaffSaveData> mStaffViewLins = new ArrayList<>();
    //黑色弧形延音线
    private List<Tia> mBlackTia = new ArrayList<>();
    //红色弧形延音线
    private List<Tia> mRedTia = new ArrayList<>();
    //小结线
    private List<StaffSaveData> mFristMeasureLins = new ArrayList<>();
    private List<StaffSaveData> mSecoundMeasureLins = new ArrayList<>();

    //全音符符头
    private List<HeadData> mWholeHead;
    //二分音符符头
    private List<HeadData> mHalfHead;
    //四分音符符头
    private List<HeadData> mHead;
    //延音符
    private List<Dot> mDot;
    //头部间线（超出五线谱范围的）
    private List<StaffSaveData> mHeadLins;
    //符杠
    private List<StaffSaveData> mLins;
    //底部连音线
    private List<StaffSaveData> mSlurLins;
    //八分音符的符尾
    private List<Tial> mTial;
    //降音b
    private List<Dwon> mDwon;
    //休止符号(直线)
    private List<Rest> mRest;
    //休止符号(贝塞尔曲线)
    private List<Tia> mRestTia;
    /*************二次保存****************/
    //全音符符头
    private List<List<HeadData>> mAllWholeHead = new ArrayList();
    //二分音符符头
    private List<List<HeadData>> mAllHalfHead = new ArrayList();
    //四分音符符头
    private List<List<HeadData>> mAllHead = new ArrayList();
    //延音符
    private List<List<Dot>> mAllDot = new ArrayList();


    //头部间线（超出五线谱范围的）
    private List<List<StaffSaveData>> mAllHeadLins = new ArrayList<>();
    //符杠
    private List<List<StaffSaveData>> mAllLins = new ArrayList<>();

    //底部连音线
    private List<List<StaffSaveData>> mAllSlurLins = new ArrayList<>();
    //八分音符的符尾
    private List<List<Tial>> mAllTial = new ArrayList<>();
    //降音b
    private List<List<Dwon>> mAllDwon = new ArrayList();
    //休止符号(直线)
    private List<List<Rest>> mAllRest = new ArrayList();
    //休止符号(贝塞尔曲线)
    private List<List<Tia>> mAllRestTia = new ArrayList();

    private float mProgressLeft = 0;

    private float lastX = 0;

    private IPlayEnd mIPlayEnd;

    /**
     * ******************************************************************
     */
    public StaffView(Context context) {
        this(context, null);
    }

    public StaffView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaffView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        holder = getHolder();//获取SurfaceHolder对象，同时指定callback
        holder.addCallback(this);
        initPaint();
    }

    /**
     * 初始化五线谱基本数据
     */
    private void init() {
        if (mLinsRoomWidth != 0) return;
        //6个五线谱，每个五线谱4隔间
        mLinsRoomWidth = 240 / 24;
        mLinsRoomWidth2 = mLinsRoomWidth * 2;
        mLinsRoomWidth3 = mLinsRoomWidth * 3;
        mLinsRoomWidth4 = mLinsRoomWidth * 4;
        mLinsRoomWidth5 = mLinsRoomWidth * 5;
        mLinsRoomWidth6 = mLinsRoomWidth * 6;
        mLinsRoomWidth7 = mLinsRoomWidth * 7;
        mLinsRoomWidth8 = mLinsRoomWidth * 8;
        mLayoutCenterHeight = 240 / 2;
        twoStaff_fristLins_up = mLayoutCenterHeight - mLinsRoomWidth4;
        twoStaff_threeLins_up = mLayoutCenterHeight - mLinsRoomWidth6;
        twoStaff_fiveLins_up = mLayoutCenterHeight - mLinsRoomWidth8;

        twoStaff_fristLins_down = mLayoutCenterHeight + mLinsRoomWidth8;
        twoStaff_threeLins_down = mLayoutCenterHeight + mLinsRoomWidth6;
        twoStaff_fiveLins_down = mLayoutCenterHeight + mLinsRoomWidth4;
        initSignScan();
        initRiseDrop();
        mBeamPaint.setStrokeWidth(mLinsRoomWidth / 2);
    }

    /**
     * 初始化音符的缩放比
     */
    private void initSignScan() {
        Drawable treble = ContextCompat.getDrawable(mContext, R.drawable.treble);
        Drawable bass = ContextCompat.getDrawable(mContext, R.drawable.bass);
        Drawable two = ContextCompat.getDrawable(mContext, R.drawable.two);
        Drawable twelve = ContextCompat.getDrawable(mContext, R.drawable.twelve);
        int trebleHeight = treble.getIntrinsicHeight();
        int trebleWidth = treble.getIntrinsicWidth();
        int bassWidth = bass.getIntrinsicWidth();
        int bassHeight = bass.getIntrinsicHeight();
        int numWidth = two.getIntrinsicWidth();
        int numHeight = two.getIntrinsicHeight();
        int twelveWidth = twelve.getIntrinsicWidth();
        int twelveHeight = twelve.getIntrinsicHeight();
        mTrebleScale = (float) mLinsRoomWidth7 / trebleHeight;
        mBassScale = (float) mLinsRoomWidth3 / bassHeight;
        mNumScale = (float) mLinsRoomWidth2 / numHeight;
        mTwelveScale = (float) mLinsRoomWidth2 / twelveHeight;
        mTrebleWidth = (int) (trebleWidth * mTrebleScale);
        mBassWidth = (int) (bassWidth * mBassScale);
        mNumWidth = (int) (numWidth * mNumScale);
        mTwelveWidth = (int) (twelveWidth * mTwelveScale);

        mTrebleHeight = (int) (trebleHeight * mTrebleScale);
        mBassHeight = (int) (bassHeight * mBassScale);
        mNumHeight = (int) (numHeight * mNumScale);
        mTwelveHeight = (int) (twelveHeight * mTwelveScale);

        trebleDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.treble),
                Gravity.NO_GRAVITY, mTrebleScale, mTrebleScale).getDrawable();
        bassDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.bass),
                Gravity.NO_GRAVITY, mBassScale, mBassScale).getDrawable();

        twoDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.two),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        threeDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.three),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        fourDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.four),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        sixDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.six),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        eightDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.eight),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        nineDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.nine),
                Gravity.NO_GRAVITY, mNumScale, mNumScale).getDrawable();
        twelveDrawable = new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.twelve),
                Gravity.NO_GRAVITY, mTwelveScale, mTwelveScale).getDrawable();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {

        mBlackPaint = new Paint();
        mBlackPaint.setAntiAlias(true);
        mBlackPaint.setColor(getResources().getColor(R.color.black));
        mBlackPaint.setStyle(Paint.Style.FILL);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(getResources().getColor(R.color.white));
        mWhitePaint.setStyle(Paint.Style.FILL);

        mTailPaint = new Paint();
        mTailPaint.setAntiAlias(true);
        mTailPaint.setColor(getResources().getColor(R.color.black));
        mTailPaint.setStyle(Paint.Style.STROKE);
        mTailPaint.setStrokeWidth(STAFF_LINS_WSITH * 2);

        mLinsPaint = new Paint();
        mLinsPaint.setAntiAlias(true);
        mLinsPaint.setColor(getResources().getColor(R.color.black));
        mLinsPaint.setStyle(Paint.Style.STROKE);
        mLinsPaint.setStrokeWidth(STAFF_LINS_WSITH);

        mLinsPaint2 = new Paint();
        mLinsPaint2.setAntiAlias(true);
        mLinsPaint2.setColor(getResources().getColor(R.color.black));
        mLinsPaint2.setStyle(Paint.Style.STROKE);
        mLinsPaint2.setStrokeWidth(STAFF_LINS_WSITH * 2);

        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setColor(getResources().getColor(R.color.red));
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeWidth(STAFF_LINS_WSITH);

        mCrudePaint = new Paint();
        mCrudePaint.setAntiAlias(true);
        mCrudePaint.setColor(getResources().getColor(R.color.black));
        mCrudePaint.setStyle(Paint.Style.STROKE);
        mCrudePaint.setStrokeWidth(STAFF_LINS_WSITH * 3);

        mBeamPaint = new Paint();
        mBeamPaint.setAntiAlias(true);
        mBeamPaint.setColor(getResources().getColor(R.color.black));
        mBeamPaint.setStyle(Paint.Style.STROKE);

        mBluePaint = new Paint();
        mBluePaint.setAntiAlias(true);
        mBluePaint.setColor(getResources().getColor(R.color.violet));
        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setAlpha(70);//0-255
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 初始化升降音位置
     */
    private void initRiseDrop() {
        //        升音#底部横线位置
        HighRise = new ArrayList<>();
        LowRise = new ArrayList<>();
        HighRise.add(-mLinsRoomWidth3);
        LowRise.add(-mLinsRoomWidth2);

        HighRise.add(-mLinsRoomWidth2);
        LowRise.add(-mLinsRoomWidth);

        HighRise.add(-mLinsRoomWidth4);
        LowRise.add(-mLinsRoomWidth3);

        HighRise.add(-mLinsRoomWidth5 / 2);
        LowRise.add(-mLinsRoomWidth3 / 2);

        HighRise.add(-mLinsRoomWidth);
        LowRise.add(0);

        HighRise.add(-mLinsRoomWidth3);
        LowRise.add(-mLinsRoomWidth2);

        HighRise.add(-mLinsRoomWidth3 / 2);
        LowRise.add(-mLinsRoomWidth / 2);
//        降音b顶部的位置
        HighDrop = new ArrayList<>();
        LowDrop = new ArrayList<>();
        HighDrop.add(-mLinsRoomWidth4);
        HighDrop.add(-mLinsRoomWidth5 - mLinsRoomWidth / 2);
        HighDrop.add(-mLinsRoomWidth7 / 2);
        HighDrop.add(-mLinsRoomWidth5);
        HighDrop.add(-mLinsRoomWidth3);
        HighDrop.add(-mLinsRoomWidth4 - mLinsRoomWidth / 2);
        HighDrop.add(-mLinsRoomWidth5 / 2);

        LowDrop.add(-mLinsRoomWidth3);
        LowDrop.add(-mLinsRoomWidth4 - mLinsRoomWidth / 2);
        LowDrop.add(-mLinsRoomWidth5 / 2);
        LowDrop.add(-mLinsRoomWidth4);
        LowDrop.add(-mLinsRoomWidth2);
        LowDrop.add(-mLinsRoomWidth3 - mLinsRoomWidth / 2);
        LowDrop.add(-mLinsRoomWidth3 / 2);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当SurfaceView被创建时，将画图Thread启动起来。

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当SurfaceView被销毁时，释放资源。
        synchronized (this) {
            isMove = false;
        }

    }

    public void setiPlayEnd(IPlayEnd iPlayEnd) {
        mIPlayEnd = iPlayEnd;
    }


    class MysurfaceviewThread extends Thread {

        public MysurfaceviewThread() {
            super();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while (true) {
                if (isMove) {
                    synchronized (holder) {
                        //锁定canvas
                        mCanvas = holder.lockCanvas();
                        //canvas 执行一系列画的动作
                        if (mCanvas != null) {
                            mCanvas.drawColor(Color.WHITE);
                            if (isSaveData) {
                                if (mAttributess != null) {
                                    mPath = new Path();
                                    moveLenth = 0;
                                    drawStaffLines(mAttributess, mCanvas, (fristSingLenth.size() == 0 || moveLenth < fristSingLenth.get(1)));
                                    //初始化五线谱(条数)
                                    MyLogUtils.e(TAG, "绘制");
                                    //清除所有数据
                                    initData();
                                    //绘制音符
                                    drawSgin(mCanvas);
                                    isSaveData = false;
                                    lastX = mFristStaffWidth;
                                    MyLogUtils.e(TAG, "绘制结束");
                                }
                            } else {
                                long time = System.currentTimeMillis();
                                if (mProgressLeft < Math.max(0, mLayoutWidth - 1920)) {
//                                if (mProgressLeft < Math.max(0, mLayoutWidth - 500)) {
                                    if (mProgressLeft < 0) {
                                        isMove = false;
                                        if (mIPlayEnd != null) {
                                            new Handler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mIPlayEnd.end();
                                                }
                                            });
                                        }
                                    }
                                    mProgressLeft += mLenth * mReta;
                                } else {
                                    moveLenth += mLenth * mReta;
                                }
                                if (mAttributess != null) {
                                    mPath = new Path();
                                    drawStaffLines(mAttributess, mCanvas, (fristSingLenth.size() == 0 || moveLenth < fristSingLenth.get(1)));
                                    selectDraw(mCanvas);
                                    if (mProgressLeft > 0) {
                                        setProgresBar(mCanvas, mProgressLeft);
                                    }
                                }
                                if (moveLenth + mProgressLeft >= lastX) {
                                    mProgressLeft = -mLenth * mReta;
                                    moveLenth = 0;
                                }
                                try {
                                    MyLogUtils.e(TAG, "绘制时间" + (System.currentTimeMillis() - time));
                                    Thread.sleep(Math.max(0, (100 - (System.currentTimeMillis() - time))));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            //释放canvas对象，并发送到SurfaceView
                            if (mCanvas != null) {
                                holder.unlockCanvasAndPost(mCanvas);
                                mCanvas = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    /**
     * 设置进度条
     *
     * @param canvas
     */
    private void setProgresBar(Canvas canvas, float mLeft) {
        if (mProgessRectF == null) {
            mProgessRectF = new RectF();
        }
        mProgessRectF.left = mLeft;
        mProgessRectF.top = 10;
        mProgessRectF.right = mLeft + 10;
//        mProgessRectF.right = mLeft + 5;
        if (isTowStaff) {
            mProgessRectF.bottom = mLayoutHeight - 10;
            canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
        } else {
            mProgessRectF.bottom = twoStaff_fristLins_up + mLinsRoomWidth * 4;
        }
        canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
    }

    /**
     * 部分绘制
     *
     * @param canvas
     */
    private void selectDraw(Canvas canvas) {

        //10条间线
        for (int i = 0; i < mStaffViewLins.size(); i++) {
            StaffSaveData data = mStaffViewLins.get(i);
            canvas.drawLine(Math.max(data.getLefts() - moveLenth, 0),
                    data.getTops(),
                    Math.min(data.getRightss() - moveLenth, mLayoutWidth),
                    data.getBottoms(), mLinsPaint);
        }
//            黑色弧形延音线
        mPath.reset();
        for (int i = 0; i < mBlackTia.size(); i++) {
            Tia tia = mBlackTia.get(i);
            if (tia.getRightss() - moveLenth < 0) continue;
            if (tia.getLefts() - moveLenth > mLayoutWidth) continue;
            mPath.moveTo(tia.getLefts() - moveLenth, tia.getTops());
            mPath.quadTo(tia.getX() - moveLenth, tia.getY(), tia.getRightss() - moveLenth, tia.getBottoms());
            canvas.drawPath(mPath, mLinsPaint);
        }
//        红色弧形延音线
        mPath.reset();
        for (int i = 0; i < mRedTia.size(); i++) {
            Tia tia = mRedTia.get(i);
            if (tia.getLefts() - moveLenth > mLayoutWidth) continue;
            if (tia.getRightss() - moveLenth < 0) continue;
            mPath.moveTo(tia.getLefts() - moveLenth, tia.getTops());
            mPath.quadTo(tia.getX() - moveLenth, tia.getY(),
                    tia.getRightss() - moveLenth, tia.getBottoms());
            canvas.drawPath(mPath, mRedPaint);
        }
        int num = 0;
        int max = mAllHead.size();
        //一小节的分割线
        for (int i = num; i < max; i++) {
            if (fristSingLenth.get(i) - moveLenth > mLayoutWidth) break;
            StaffSaveData data = mFristMeasureLins.get(i);
            if (data.getLefts() - moveLenth < 0) continue;
            if (data.getLefts() - moveLenth > mLayoutWidth) continue;
            canvas.drawLine(data.getLefts() - moveLenth, data.getTops(), data.getRightss() - moveLenth, data.getBottoms(), mLinsPaint);
        }
        if (isTowStaff) {
            //每一小节的分割线
            for (int i = num; i < max; i++) {
                if (fristSingLenth.get(i) - moveLenth > mLayoutWidth) break;
                StaffSaveData data = mSecoundMeasureLins.get(i);
                if (data.getLefts() - moveLenth < 0) continue;
                if (data.getLefts() - moveLenth > mLayoutWidth) continue;
                canvas.drawLine(data.getLefts() - moveLenth, data.getTops(), data.getRightss() - moveLenth, data.getBottoms(), mLinsPaint);
            }
        }
        for (int j = num; j < max; j++) {
            //绘制全音符符头
            List<HeadData> wholeHead = mAllWholeHead.get(j);
            for (int i = 0; i < wholeHead.size(); i++) {
                HeadData data = wholeHead.get(i);
                if (data.getLeft1() - moveLenth > mLayoutWidth) continue;
                if (data.getRight1() - moveLenth < 0) continue;
                canvas.save();
                RectF rectFs = new RectF(data.getLeft1() - moveLenth,
                        data.getTop1(),
                        data.getRight1() - moveLenth,
                        data.getBottom1());
                canvas.drawOval(rectFs, mBlackPaint);
                canvas.rotate(30, data.getPx() - moveLenth, data.getPy());
                RectF rectF = new RectF(data.getLeft2() - moveLenth,
                        data.getTop2(),
                        data.getRight2() - moveLenth,
                        data.getBottom2());
                canvas.drawOval(rectF, mWhitePaint);
                canvas.restore();
            }
            //绘制二分音符符头
            List<HeadData> halfHead = mAllHalfHead.get(j);
            for (int i = 0; i < halfHead.size(); i++) {
                HeadData data = halfHead.get(i);
                if (data.getLeft1() - moveLenth > mLayoutWidth) continue;
                if (data.getRight1() - moveLenth < 0) continue;
                canvas.save();
                RectF rectFs = new RectF(data.getLeft1() - moveLenth,
                        data.getTop1(),
                        data.getRight1() - moveLenth,
                        data.getBottom1());
                canvas.rotate(-30, data.getPx() - moveLenth, data.getPy());
                canvas.drawOval(rectFs, mBlackPaint);
                canvas.restore();
                canvas.save();
                RectF rectF = new RectF(data.getLeft2() - moveLenth,
                        data.getTop2(),
                        data.getRight2() - moveLenth,
                        data.getBottom2());
                canvas.rotate(-30, data.getPx() - moveLenth, data.getPy());
                canvas.drawOval(rectF, mWhitePaint);
                canvas.restore();
            }
            //绘制四分音符符头
            List<HeadData> head = mAllHead.get(j);
            for (int i = 0; i < head.size(); i++) {
                HeadData data = head.get(i);
                if (data.getLeft1() - moveLenth > mLayoutWidth) continue;
                if (data.getRight1() - moveLenth < 0) continue;
                canvas.save();
                RectF rectFs = new RectF(data.getLeft1() - moveLenth,
                        data.getTop1(),
                        data.getRight1() - moveLenth,
                        data.getBottom1());
                canvas.rotate(-30, data.getPx() - moveLenth, data.getPy());//向上旋转30度
                canvas.drawOval(rectFs, mBlackPaint);
                canvas.restore();
            }
            //绘制延音dot
            List<Dot> dots = mAllDot.get(j);
            for (int i = 0; i < dots.size(); i++) {
                Dot dot = dots.get(i);
                if (dot.getCx() - moveLenth < 0) continue;
                if (dot.getCx() - moveLenth > mLayoutWidth) continue;
                canvas.drawCircle(dot.getCx() - moveLenth, dot.getCy(), mLinsRoomWidth / 3, mBlackPaint);
            }
            //绘制音符短间线
            List<StaffSaveData> headLins = mAllHeadLins.get(j);
            for (int i = 0; i < headLins.size(); i++) {
                StaffSaveData data = headLins.get(i);
                if (data.getLefts() - moveLenth > mLayoutWidth) continue;
                if (data.getRightss() - moveLenth < 0) continue;
                drawLins(canvas, data.getLefts() - moveLenth, data.getTops(), data.getRightss() - moveLenth, data.getBottoms());
//            MyLogUtils.e(TAG, "绘制短线");
            }
            //绘制符杠
            List<StaffSaveData> lins = mAllLins.get(j);
            for (int i = 0; i < lins.size(); i++) {
                StaffSaveData data = lins.get(i);
                if (data.getLefts() - moveLenth > mLayoutWidth) continue;
                if (data.getRightss() - moveLenth < 0) continue;
                drawLins(canvas, data.getLefts() - moveLenth, data.getTops(), data.getRightss() - moveLenth, data.getBottoms());
            }
            //绘制符尾连音符
            List<StaffSaveData> slurLins = mAllSlurLins.get(j);
            for (int i = 0; i < slurLins.size(); i++) {
                StaffSaveData data = slurLins.get(i);
                if (data.getLefts() - moveLenth > mLayoutWidth) continue;
                if (data.getRightss() - moveLenth < 0) continue;
                canvas.drawLine(data.getLefts() - moveLenth,
                        data.getTops(),
                        data.getRightss() - moveLenth,
                        data.getBottoms(), mBeamPaint);
            }
            mPath.reset();
            List<Tial> tial = mAllTial.get(j);
            for (int i = 0; i < tial.size(); i++) {
                Tial tia = tial.get(i);
                if (tia.getX() - moveLenth > mLayoutWidth) continue;
                if (tia.getX3() - moveLenth < 0) continue;
                mPath.moveTo(tia.getX() - moveLenth, tia.getY());
                mPath.cubicTo(tia.getX1() - moveLenth, tia.getY1(),
                        tia.getX2() - moveLenth, tia.getY2(),
                        tia.getX3() - moveLenth, tia.getY3());
                canvas.drawPath(mPath, mTailPaint);
            }
            List<Dwon> dwons = mAllDwon.get(j);
            for (int i = 0; i < dwons.size(); i++) {
                Dwon dwon = dwons.get(i);
                if (dwon.getLeft() - moveLenth > mLayoutWidth) continue;
                if (dwon.getRight() - moveLenth < 0) continue;
                mPath.moveTo(dwon.getLeft() - moveLenth, dwon.getTop());
                mPath.quadTo(dwon.getX() - moveLenth, dwon.getY(),
                        dwon.getRight() - moveLenth, dwon.getBottom());
                canvas.drawPath(mPath, mTailPaint);
                drawLins(canvas, dwon.getX1() - moveLenth, dwon.getY1(),
                        dwon.getX2() - moveLenth, dwon.getY2());
            }
            List<Rest> restS = mAllRest.get(j);
            for (int i = 0; i < restS.size(); i++) {
                Rest rest = restS.get(i);
                if (rest.getLeft() - moveLenth > mLayoutWidth) continue;
                if (rest.getRight() - moveLenth < 0) continue;
                canvas.drawLine(rest.getLeft() - moveLenth, rest.getTop(),
                        rest.getRight() - moveLenth, rest.getBottom(), mCrudePaint);
            }
            List<Tia> restTia = mAllRestTia.get(j);
            for (int i = 0; i < restTia.size(); i++) {
                Tia tia = restTia.get(i);
                if (tia.getLefts() - moveLenth > mLayoutWidth) continue;
                if (tia.getRightss() - moveLenth < 0) continue;
                mPath.moveTo(tia.getLefts() - moveLenth, tia.getTops());
                mPath.quadTo(tia.getX() - moveLenth, tia.getY(), tia.getRightss() - moveLenth, tia.getBottoms());
                canvas.drawPath(mPath, mLinsPaint);
            }
        }
    }

    private void initData() {
        mTie.clear();
        mSlur.clear();
        mStaffViewLins.clear();
        mBlackTia.clear();
        mRedTia.clear();
        mFristMeasureLins.clear();
        mSecoundMeasureLins.clear();
        fristSingLenth.clear();

        mAllWholeHead.clear();
        mAllHalfHead.clear();
        mAllHead.clear();
        mAllDot.clear();
        mAllHeadLins.clear();
        mAllLins.clear();
        mAllSlurLins.clear();
        mAllTial.clear();
        mAllDwon.clear();
        mAllRest.clear();
        mAllRestTia.clear();
    }


    /**
     * 绘制符号
     */
    private void drawSgin(Canvas canvas) {
        int size = mFristStaffData.size();
        for (int j = 0; j < size; j++) {
            initSgin();
//          保存每一小节第一一条五线谱的以一个元素的位置
            fristSingLenth.add(mFristStaffWidth);
            List<MeasureBase> base = mFristStaffData.get(j).getMeasure();
            List<MeasureBase> base1 = mSecondStaffData.get(j).getMeasure();
            int baseSize = base.size();
            int base1Size = base1.size();
            //第一个五线谱
            for (int k = 0; k < baseSize; k++) {
                Notes nots = base.get(k).getNotes();
                if (nots != null) {
                    if (k + 1 < base.size()) {
                        drawNotes(canvas, nots, true, base.get(k + 1).getNotes());
                    } else {
                        drawNotes(canvas, nots, true, null);
                    }
                }
            }
            if (isTowStaff) {
                mScendStaffWidth = mFristStaffWidth - mBackUPData.get(j) * mSpeedLenth;
                //第二条五线谱
                for (int k = 0; k < base1Size; k++) {
                    Notes nots = base1.get(k).getNotes();
                    if (nots != null) {
                        if (k + 1 < base1.size()) {
                            drawNotes(canvas, nots, false, base1.get(k + 1).getNotes());
                        } else {
                            drawNotes(canvas, nots, false, null);
                        }
                    }
                }
            }
            mFristStaffWidth = Math.max(mFristStaffWidth, mScendStaffWidth) + mLinsRoomWidth2;
            mScendStaffWidth = mFristStaffWidth;
            drawMeasureLins(canvas, j, mFristStaffData.size() - 1);
            if (j != mFristStaffData.size() - 1) {
                mFristStaffWidth += mSpeedLenth;
                mScendStaffWidth += mSpeedLenth;
            }
            mAllWholeHead.add(mWholeHead);
            mAllHalfHead.add(mHalfHead);
            mAllHead.add(mHead);
            mAllDot.add(mDot);
            mAllHeadLins.add(mHeadLins);
            mAllLins.add(mLins);
            mAllSlurLins.add(mSlurLins);
            mAllTial.add(mTial);
            mAllDwon.add(mDwon);
            mAllRest.add(mRest);
            mAllRestTia.add(mRestTia);
        }
        drawStaffLins(canvas);
    }

    private void initSgin() {
        mWholeHead = new ArrayList<>();
        mHalfHead = new ArrayList<>();
        mHead = new ArrayList<>();
        mDot = new ArrayList<>();
        mHeadLins = new ArrayList<>();
        mLins = new ArrayList<>();
        mSlurLins = new ArrayList<>();
        mTial = new ArrayList<>();
        mDwon = new ArrayList<>();
        mRest = new ArrayList<>();
        mRestTia = new ArrayList<>();

        legatosMap.clear();
        forwardHook.clear();
        legatosMap.clear();
    }

    /**
     * 绘制间线
     *
     * @param canvas
     */
    private void drawStaffLins(Canvas canvas) {
        //第一条五线谱的五条间线
        canvas.drawLine(mLinsRoomWidth2, twoStaff_fiveLins_up, mFristStaffWidth, twoStaff_fiveLins_up, mLinsPaint);
        canvas.drawLine(mLinsRoomWidth2, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth, mLinsPaint);
        canvas.drawLine(mLinsRoomWidth2, twoStaff_threeLins_up, mFristStaffWidth, twoStaff_threeLins_up, mLinsPaint);
        canvas.drawLine(mLinsRoomWidth2, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mLinsPaint);
        canvas.drawLine(mLinsRoomWidth2, twoStaff_fristLins_up, mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
        mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fiveLins_up, mFristStaffWidth, twoStaff_fiveLins_up));
        mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth));
        mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_threeLins_up, mFristStaffWidth, twoStaff_threeLins_up));
        mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth));
        mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fristLins_up, mFristStaffWidth, twoStaff_fristLins_up));
        if (isTowStaff) {
            //第一条五线谱的五条间线
            canvas.drawLine(mLinsRoomWidth2, twoStaff_fiveLins_down, mFristStaffWidth, twoStaff_fiveLins_down, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2, twoStaff_fiveLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2, twoStaff_threeLins_down, mFristStaffWidth, twoStaff_threeLins_down, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2, twoStaff_threeLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2, twoStaff_fristLins_down, mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
            mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fiveLins_down, mFristStaffWidth, twoStaff_fiveLins_down));
            mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fiveLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth));
            mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_threeLins_down, mFristStaffWidth, twoStaff_threeLins_down));
            mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_threeLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth));
            mStaffViewLins.add(new StaffSaveData(mLinsRoomWidth2, twoStaff_fristLins_down, mFristStaffWidth, twoStaff_fristLins_down));
        }
    }

    /**
     * 绘制小节线
     */

    private void drawMeasureLins(Canvas canvas, int j, int i) {
        if (j < i) {
            //每小节的竖直分割线
            canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                    mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
            mFristMeasureLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fiveLins_up, mFristStaffWidth, twoStaff_fristLins_up));
            if (isTowStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down,
                        mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
                mSecoundMeasureLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fiveLins_down,
                        mFristStaffWidth, twoStaff_fristLins_down));

            }
        } else {
            if (isTowStaff) {
                //如果是两条五线谱，绘制最后一小节的尾线
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
                mFristMeasureLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_down));
                mSecoundMeasureLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_down));
            } else {
                //如果是一条五线谱，绘制最后一小节的尾线
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
                mFristMeasureLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_up));
            }
        }
    }


    /**
     * 绘制音符
     *
     * @param canvas
     * @param notes
     * @param isFristStaff 是否是第一条五线谱
     * @param note         下一个音符
     */
    private void drawNotes(Canvas canvas, Notes notes, boolean isFristStaff, Notes note) {
        if (notes.getRest()) {
            //休止符
            String type = notes.getType();
            if (type == null) type = getType(notes);
            drawRest(type, canvas, !isFristStaff);
        } else if (notes.getPitch() != null) {
            if (note != null) {
                if (note.getChord() && note.getStems() != null) {
                    if (note.getStems().equals("dwon")) {
                        isDrawTial = false;
                    } else {
                        isDrawTial = false;
                    }
                } else {
                    isDrawTial = true;
                }
            }
            //音符
            drawPitch(canvas, notes, isFristStaff);
        }
        if (note != null && note.getChord()) {
            //和弦不计算宽度
        } else {
            //每个音符按3个间的宽度来算
            if (isFristStaff) {
                mFristStaffWidth += Integer.valueOf(notes.getDuration()) * mSpeedLenth;
            } else {
                mScendStaffWidth += Integer.valueOf(notes.getDuration()) * mSpeedLenth;
            }
        }
    }

    /**
     * 绘制音符
     *
     * @param canvas
     * @param notes
     * @param isFristStaff 第一条五线谱
     */
    private void drawPitch(Canvas canvas, Notes notes, boolean isFristStaff) {
        //加点（延音）
        boolean isAddDot = notes.getDot();
        //符尾向下
        boolean isDwon = false;
        if (notes.getStems() != null) {
            isDwon = notes.getStems().equals("down");
        }
        int y = getPatchPosiotion(notes, isFristStaff);
        if (notes.getPitch().getAlter() != null) {
            //绘制升降音
            drawfitts(canvas, isFristStaff, y, notes.getPitch().getAlter());
        }
        String type = notes.getType();
        if (type == null) {
            type = getType(notes);
        }
        if (notes.getTie() != null) {
            //绘制弧形延音线
            drawTie(canvas, y, notes, isFristStaff, type);
        }
        if (notes.getSlur() != null) {
            //绘制红色弧形延音线
            drawSlur(canvas, y, notes, isFristStaff, type);
        }
        if (type == null) return;
        //绘制音符符头
        drawHeads(type, isAddDot, canvas, y, isFristStaff);
        //符杠的X坐标
        float tialX = getTialX(isFristStaff, isDwon);
        int tialStartY = isDwon ? y + mLinsRoomWidth : y + mLinsRoomWidth / 4;
        int tialStopY = getTialY(type, isDwon, y);
        if (notes.getBeam() == null) {
            //有和音返回
//            if (isSteam) return;
            //绘制符杠
            drawLins(canvas, tialX, tialStartY, tialX, tialStopY);
            mLins.add(new StaffSaveData(tialX, tialStartY, tialX, tialStopY));
            if (isDrawTial) {
                //绘制符号尾部（八分音符以后）
                drawTial(canvas, isFristStaff ? mFristStaffWidth : mScendStaffWidth, y, isDwon, type, notes);
            }
//            if (isFristStaff)MyLogUtils.e(TAG,"tial："+mFristStaffWidth);
        } else {
//            if (isFristStaff)MyLogUtils.e(TAG,"tialS："+mFristStaffWidth);
            //绘制符尾连音符
            drawLegato(notes.getBeam(), isDwon, tialX, tialStartY, tialStopY, canvas, notes.getChord());
        }
    }

    /**
     * 获取音符符尾尾坐标
     *
     * @param type
     * @param isDwon
     * @param y
     * @return
     */
    private int getTialY(String type, boolean isDwon, int y) {
        switch (type) {
            case "half":
            case "quarter":
            case "eighth":
                if (isDwon) {
                    return y + mLinsRoomWidth4;
                } else {
                    return y - mLinsRoomWidth3;
                }
            case "16th":
                if (isDwon) {
                    return y + mLinsRoomWidth5;
                } else {
                    return y - mLinsRoomWidth4;
                }
            case "32th":
                if (isDwon) {
                    return y + mLinsRoomWidth6;
                } else {
                    return y - mLinsRoomWidth5;
                }
            case "64th":
                if (isDwon) {
                    return y + mLinsRoomWidth7;
                } else {
                    return y - mLinsRoomWidth6;
                }
            default:
                return y;
        }
    }

    /**
     * 获取符杠X值
     *
     * @param isFristStaff
     * @param isDwon
     * @return
     */
    private float getTialX(boolean isFristStaff, boolean isDwon) {
        float X = isFristStaff ? mFristStaffWidth : mScendStaffWidth;
        if (isDwon) {
            return X + mLinsRoomWidth;
        } else {
            return X + mLinsRoomWidth2;
        }
    }


    /**
     * 绘制所有符头
     *
     * @param type
     * @param isAddDot
     * @param canvas
     * @param y
     * @param isFristStaff
     */
    private void drawHeads(String type, boolean isAddDot, Canvas canvas, int y, boolean isFristStaff) {
        float x;
        if (isFristStaff) {
            x = mFristStaffWidth;
        } else {
            x = mScendStaffWidth;
        }
        switch (type) {
            case "whole":
                //全音符
                drawAllHollowHeads(isAddDot, canvas, x, y, isFristStaff);
                break;
            case "half":
                //二分音符
                drawTowHollowHeads(isAddDot, canvas, x, y, isFristStaff);
                break;
            default:
                drawBlackHeads(isAddDot, canvas, x, y, isFristStaff);
                break;
        }
    }

    private String getType(Notes notes) {
        if (beats == 0) return null;
        if (divisions == 0) return null;
        String string = null;
        Double who = Double.valueOf(notes.getDuration()) / (beats * divisions);
        for (int i = 0; i < 7; i++) {
            if (who == typeNum[i]) {
                return typeString[i];
            }
            if (i > 0 && who > typeNum[i]) {
                return typeString[typeNum[i - 1] - who > who - typeNum[i] ? i - 1 : i];
            }
        }
        return string;
    }

    /**
     * 红色延音线
     *
     * @param canvas
     * @param i
     * @param notes
     * @param isFristStaff
     * @param type
     */
    private void drawSlur(Canvas canvas, int i, Notes notes, boolean isFristStaff, String type) {
        mPath = new Path();
        float x, y;
        if (isFristStaff) {
            x = mFristStaffWidth + mLinsRoomWidth;
        } else {
            x = mScendStaffWidth + mLinsRoomWidth;
        }
        boolean isUp;
        if (notes.getStems() != null && notes.getStems().equals("up")) {
            y = i + mLinsRoomWidth;
            isUp = true;
        } else {
            y = i - mLinsRoomWidth;
            isUp = false;
        }
        int size = notes.getSlur().size();
        for (int j = 0; j < size; j++) {
            switch (notes.getSlur().get(j)) {
                case "start":
                    if (mSlur == null) {
                        mSlur = new ArrayList<>();
                    }
                    mSlur.add(new Tie(x + mLinsRoomWidth2, y, isUp));
                    break;
                case "stop":
                    if (mSlur.size() == 0) return;
                    Tie ties = mSlur.get(0);
                    mPath.moveTo(ties.getX(), ties.getY());
                    if (ties.isUp() == isUp) {
                        mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y);
                        mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                (ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y));
                    } else {
                        y = getTieY(ties, y, type);
                        if (Math.abs(ties.getX() - x) / mLinsRoomWidth3 > Math.abs(ties.getY() - y) / mLinsRoomWidth) {
                            mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y);
                            mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                    (ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y));
                        } else {
                            y = getTieY(ties, y, type);
                            if (Math.abs(ties.getX() - x) / mLinsRoomWidth4 > Math.abs(ties.getY() - y) / mLinsRoomWidth) {
                                mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y);
                                mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                        (ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y));
                            } else {
                                if (!isUp) {
                                    if (ties.getY() < y) {
                                        mPath.quadTo(ties.getX() + (x - ties.getX()) / 4,
                                                ties.getY() + (y - ties.getY()) * 3 / 4,
                                                x - mLinsRoomWidth, y);
                                        mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                                ties.getX() + (x - ties.getX()) / 4,
                                                ties.getY() + (y - ties.getY()) * 3 / 4,
                                                x - mLinsRoomWidth, y));
                                    } else {
                                        mPath.quadTo(ties.getX() + (x - ties.getX()) * 3 / 4,
                                                ties.getY() - (y - ties.getY()) / 4,
                                                x - mLinsRoomWidth, y);
                                        mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                                ties.getX() + (x - ties.getX()) * 3 / 4,
                                                ties.getY() - (y - ties.getY()) / 4,
                                                x - mLinsRoomWidth, y));
                                    }
                                } else {
                                    if (ties.getY() < y) {
                                        mPath.quadTo(ties.getX() + (x - ties.getX()) * 3 / 4,
                                                ties.getY() - (y - ties.getY()) / 4,
                                                x - mLinsRoomWidth, y);
                                        mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                                ties.getX() + (x - ties.getX()) * 3 / 4,
                                                ties.getY() - (y - ties.getY()) / 4,
                                                x - mLinsRoomWidth, y));
                                    } else {
                                        mPath.quadTo(ties.getX() + (x - ties.getX()) / 4,
                                                ties.getY() - (y - ties.getY()) * 3 / 4,
                                                x - mLinsRoomWidth, y);
                                        mRedTia.add(new Tia(ties.getX(), ties.getY(),
                                                ties.getX() + (x - ties.getX()) / 4,
                                                ties.getY() - (y - ties.getY()) * 3 / 4,
                                                x - mLinsRoomWidth, y));
                                    }
                                }
                            }
                        }
                    }
                    canvas.drawPath(mPath, mRedPaint);
                    mSlur.remove(0);
                    break;
            }
        }
    }

    /**
     * 黑色弧形延音线
     *
     * @param canvas
     * @param i
     * @param notes
     * @param isFristStaff
     * @param type
     */

    private void drawTie(Canvas canvas, int i, Notes notes, boolean isFristStaff, String type) {

        mPath = new Path();
        float x, y;
        if (isFristStaff) {
            x = mFristStaffWidth + mLinsRoomWidth;
        } else {
            x = mScendStaffWidth + mLinsRoomWidth;
        }
        boolean isUp;
        if (notes.getStems() != null && notes.getStems().equals("up")) {
            y = i + mLinsRoomWidth;
            isUp = true;
        } else {
            y = i - mLinsRoomWidth;
            isUp = false;
        }
        int size = notes.getTie().size();
        for (int j = 0; j < size; j++) {
            switch (notes.getTie().get(j)) {
                case "start":
                    if (mTie == null) {
                        mTie = new ArrayList<>();
                    }
                    mTie.add(new Tie(x + mLinsRoomWidth2, y, isUp));
                    break;
                case "stop":
                    if (mTie.size() == 0) return;
                    Tie ties = mTie.get(0);
                    mPath.moveTo(ties.getX(), ties.getY());
                    if (ties.isUp() == isUp) {
                        mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y);
                        mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                (ties.getX() + x) / 2,
                                getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y));
                    } else {
                        y = getTieY(ties, y, type);
                        if (Math.abs(ties.getX() - x) / mLinsRoomWidth4 > Math.abs(ties.getY() - y) / mLinsRoomWidth) {
                            mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y);
                            mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                    (ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth, y), x - mLinsRoomWidth, y));
                        } else {
                            if (!isUp) {
                                if (ties.getY() < y) {
                                    mPath.quadTo(ties.getX() + (x - ties.getX()) / 4,
                                            ties.getY() + (y - ties.getY()) * 3 / 4,
                                            x - mLinsRoomWidth, y);
                                    mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                            ties.getX() + (x - ties.getX()) / 4,
                                            ties.getY() + (y - ties.getY()) * 3 / 4,
                                            x - mLinsRoomWidth, y));
                                } else {
                                    mPath.quadTo(ties.getX() + (x - ties.getX()) * 3 / 4,
                                            ties.getY() - (y - ties.getY()) / 4,
                                            x - mLinsRoomWidth, y);
                                    mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                            ties.getX() + (x - ties.getX()) * 3 / 4,
                                            ties.getY() - (y - ties.getY()) / 4,
                                            x - mLinsRoomWidth, y));
                                }
                            } else {
                                if (ties.getY() < y) {
                                    mPath.quadTo(ties.getX() + (x - ties.getX()) * 3 / 4,
                                            ties.getY() - (y - ties.getY()) / 4,
                                            x - mLinsRoomWidth, y);
                                    mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                            ties.getX() + (x - ties.getX()) * 3 / 4,
                                            ties.getY() - (y - ties.getY()) / 4,
                                            x - mLinsRoomWidth, y));
                                } else {
                                    mPath.quadTo(ties.getX() + (x - ties.getX()) / 4,
                                            ties.getY() - (y - ties.getY()) * 3 / 4,
                                            x - mLinsRoomWidth, y);
                                    mBlackTia.add(new Tia(ties.getX(), ties.getY(),
                                            ties.getX() + (x - ties.getX()) / 4,
                                            ties.getY() - (y - ties.getY()) * 3 / 4,
                                            x - mLinsRoomWidth, y));
                                }
                            }
                        }
                    }
                    canvas.drawPath(mPath, mLinsPaint);
                    mTie.remove(0);
                    break;
            }
        }
    }

    //获取符杠的尾部
    private float getTieY(Tie ties, float y, String type) {
        int up;
        if (!ties.isUp()) {
            up = -1;
        } else {
            up = 1;
        }
        switch (type) {
            case "whole":
                return y;
            case "half":
            case "quarter":
            case "eighth":
                return y + mLinsRoomWidth5 * up;
            case "16th":
                return y + mLinsRoomWidth6 * up;
            case "32th":
                return y + mLinsRoomWidth7 * up;
            case "64th":
                return y + mLinsRoomWidth8 * up;
        }
        return y;
    }

    private float getTieZ(Tie tie, float stopX, float y) {
        float startX = tie.getX();
        float num = (stopX - startX) / mLinsRoomWidth4;
        if (num <= 0) {
            num = 1;
        } else if (num > 3) {
            num = 3;
        }
        if (tie.isUp()) {
            return y + mLinsRoomWidth * num;
        } else {
            return y - mLinsRoomWidth * num;
        }
    }

    /**
     * 绘制每个音符是否升降音
     */
    private void drawfitts(Canvas canvas, boolean isFristStaff, int y, String alter) {
        int alters = Integer.valueOf(alter);
        float x;
        if (isFristStaff) {
            x = mFristStaffWidth;
        } else {
            x = mScendStaffWidth;
        }
        switch (alters) {
            case -2:
                //重降 bb
                drawDownTune(canvas, x - mLinsRoomWidth, y - mLinsRoomWidth, y + mLinsRoomWidth);
                x += mLinsRoomWidth;
                drawDownTune(canvas, x, y - mLinsRoomWidth, y + mLinsRoomWidth);
                break;
            case -1:
                //降 b
                drawDownTune(canvas, x, y - mLinsRoomWidth, y + mLinsRoomWidth);
                break;
            case 0:
//                还原
                drawReduction(canvas, x, y);
                break;
            case 1:
//                升 #
                drawRise(canvas, x, y, 1);
                break;
            case 2:
                //重升 x
                drawRise(canvas, x, y, 2);
                break;
        }
        if (isFristStaff) {
            mFristStaffWidth += mLinsRoomWidth2;
        } else {
            mScendStaffWidth += mLinsRoomWidth2;
        }
    }

    /**
     * 绘制休止符
     */
    private void drawRise(Canvas canvas, float x, int y, int alter) {
        if (alter == 1) {
//            #
            drawLins(canvas, x + mLinsRoomWidth / 2, y + mLinsRoomWidth / 4,
                    x + mLinsRoomWidth3 / 2, y - mLinsRoomWidth / 4);
            drawLins(canvas, x + mLinsRoomWidth / 2, y + mLinsRoomWidth / 2,
                    x + mLinsRoomWidth3 / 2, y + mLinsRoomWidth / 4);
            drawLins(canvas, x + mLinsRoomWidth3 / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth3 / 4, y + +mLinsRoomWidth3 / 4);
            drawLins(canvas, x + mLinsRoomWidth5 / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth5 / 4, y + mLinsRoomWidth3 / 4);
            mLins.add(new StaffSaveData(x + mLinsRoomWidth / 2, y + mLinsRoomWidth / 4,
                    x + mLinsRoomWidth3 / 2, y - mLinsRoomWidth / 4));
            mLins.add(new StaffSaveData(x + mLinsRoomWidth / 2, y + mLinsRoomWidth / 2,
                    x + mLinsRoomWidth3 / 2, y + mLinsRoomWidth / 4));
            mLins.add(new StaffSaveData(x + mLinsRoomWidth3 / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth3 / 4, y + +mLinsRoomWidth3 / 4));
            mLins.add(new StaffSaveData(x + mLinsRoomWidth5 / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth5 / 4, y + mLinsRoomWidth3 / 4));
        } else {
            //x
            drawLins(canvas, x, y, x + mLinsRoomWidth, y + mLinsRoomWidth);
            drawLins(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth, x, y);
            mLins.add(new StaffSaveData(x, y, x + mLinsRoomWidth, y + mLinsRoomWidth));
            mLins.add(new StaffSaveData(x + mLinsRoomWidth, y + mLinsRoomWidth, x, y));
        }
    }

    /**
     * 绘制还原符号
     */
    private void drawReduction(Canvas canvas, float x, int y) {
        drawLins(canvas, x, y - mLinsRoomWidth, x, y + mLinsRoomWidth);
        drawLins(canvas, x + mLinsRoomWidth, y - mLinsRoomWidth / 2, x + mLinsRoomWidth, y + mLinsRoomWidth3 / 2);
        drawLins(canvas, x, y, x, y - mLinsRoomWidth / 2);
        drawLins(canvas, x, y + mLinsRoomWidth, x, y + mLinsRoomWidth / 2);
        mLins.add(new StaffSaveData(x, y - mLinsRoomWidth, x, y + mLinsRoomWidth));
        mLins.add(new StaffSaveData(x + mLinsRoomWidth, y - mLinsRoomWidth / 2, x + mLinsRoomWidth, y + mLinsRoomWidth3 / 2));
        mLins.add(new StaffSaveData(x, y, x, y - mLinsRoomWidth / 2));
        mLins.add(new StaffSaveData(x, y + mLinsRoomWidth, x, y + mLinsRoomWidth / 2));
    }

    /**
     * 获取音符的位置
     *
     * @param notes
     * @param isFristStaff
     */

    private int getPatchPosiotion(Notes notes, boolean isFristStaff) {
        int pa = Integer.parseInt(notes.getPitch().getOctave());
        int posiotion = 0;
        if (!isFristStaff) {
            switch (notes.getPitch().getStep()) {
                case "C":
                    posiotion = twoStaff_fiveLins_down + mLinsRoomWidth * 2;
                    break;
                case "D":
                    posiotion = twoStaff_fiveLins_down + mLinsRoomWidth * 3 / 2;
                    break;
                case "E":
                    posiotion = twoStaff_fiveLins_down + mLinsRoomWidth;
                    break;
                case "F":
                    posiotion = twoStaff_fiveLins_down + mLinsRoomWidth / 2;
                    break;
                case "G":
                    posiotion = twoStaff_fiveLins_down;
                    break;
                case "A":
                    posiotion = twoStaff_fiveLins_down - mLinsRoomWidth / 2;
                    break;
                case "B":
                    posiotion = twoStaff_fiveLins_down - mLinsRoomWidth;
                    break;
            }
            return posiotion + (mLinsRoomWidth7 / 2) * (3 - pa);
        } else {
            switch (notes.getPitch().getStep()) {
                case "C":
                    posiotion = twoStaff_fristLins_up + mLinsRoomWidth / 2;
                    break;
                case "D":
                    posiotion = twoStaff_fristLins_up;
                    break;
                case "E":
                    posiotion = twoStaff_fristLins_up - mLinsRoomWidth / 2;
                    break;
                case "F":
                    posiotion = twoStaff_fristLins_up - mLinsRoomWidth;
                    break;
                case "G":
                    posiotion = twoStaff_fristLins_up - mLinsRoomWidth3 / 2;
                    break;
                case "A":
                    posiotion = twoStaff_threeLins_up;
                    break;
                case "B":
                    posiotion = twoStaff_threeLins_up - mLinsRoomWidth / 2;
                    break;
            }
            return posiotion + (mLinsRoomWidth7 / 2) * (4 - pa);
        }

    }

    /**
     * 绘制休止符
     *
     * @param rest
     * @param canvas
     * @param inTowStaffs 是否在第二线上
     */
    private void drawRest(String rest, Canvas canvas, boolean inTowStaffs) {
        mPath = new Path();
        if (rest == null) return;
        //休止符
        switch (rest) {
            case "long":
                //4音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth, mCrudePaint);
                    mRest.add(new Rest(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth));
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth, mCrudePaint);
                    mRest.add(new Rest(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth));

                }
                break;
            case "breve":
                //2音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down, mCrudePaint);
                    mRest.add(new Rest(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down));
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up, mCrudePaint);
                    mRest.add(new Rest(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up));
                }
                break;
            case "whole":
                //1音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth, mCrudePaint);
                    mRest.add(new Rest(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth));
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth, mCrudePaint);
                    mRest.add(new Rest(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth));
                }
                break;
            case "half":
                //二分休止符
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4, mCrudePaint);
                    mRest.add(new Rest(mScendStaffWidth, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4));
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4, mCrudePaint);
                    mRest.add(new Rest(mFristStaffWidth, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4));
                }
                break;
            case "quarter":
                //四分休止符
                if (inTowStaffs) {
                    //第二条线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down
                    ));

                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down, mCrudePaint);
                    mRest.add(new Rest(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down));
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth));
                    mPath.moveTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_fristLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_fristLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth + mLinsRoomWidth, twoStaff_fristLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_fristLins_down
                    ));
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up
                    ));
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up, mCrudePaint);
                    mRest.add(new Rest(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up));
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth));
                    mPath.moveTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_fristLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_fristLins_up
                    ));
                }
                break;
            case "eighth":
                //八分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth
                    ));
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth));
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth
                    ));
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth));
                }
                break;
            case "16th":
                //16分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth
                    ));

                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_down));
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth));
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_up));

                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth));
                }
                break;
            case "32th":
                //32分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth));

                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_down));

                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth));

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth));
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth));

                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth));
                }
                break;
            case "64th":
                //64分休止符
                if (inTowStaffs) {
                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down));
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_down - mLinsRoomWidth));
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_down));
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 5);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 5));

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down));
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fiveLins_up,
                            mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up));


                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 5);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 5));

                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up));
                }
                break;
            case "128th":
                //128分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth));

                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_down));
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_down - mLinsRoomWidth));

                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_down));
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 6);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 6));

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth));
                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up, mFristStaffWidth + mLinsRoomWidth * 3,
                            twoStaff_fiveLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_fiveLins_up, mFristStaffWidth + mLinsRoomWidth * 3,
                            twoStaff_fiveLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fiveLins_up,
                            mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_up - mLinsRoomWidth));
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 6);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 6));

                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth));
                }
                break;
            case "256th":
                //256分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth));

                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 7 / 7, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fiveLins_down,
                            mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 7 / 7, twoStaff_fiveLins_down));
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_down - mLinsRoomWidth));
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_down));
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 3 / 7);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth,
                            mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 3 / 7));

                    mPath.moveTo(mScendStaffWidth, twoStaff_fristLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mScendStaffWidth, twoStaff_fristLins_down,
                            mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_down));

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth * 2,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth));
                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_fiveLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 6 / 7, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fiveLins_up,
                            mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 6 / 7, twoStaff_fiveLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_up - mLinsRoomWidth));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_up));

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 3 / 7);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth,
                            mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 3 / 7));

                    mPath.moveTo(mFristStaffWidth, twoStaff_fristLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mRestTia.add(new Tia(mFristStaffWidth, twoStaff_fristLins_up,
                            mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_up));

                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth, mLinsPaint);
                    mLins.add(new StaffSaveData(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth * 2,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth));
                }
                break;
        }
    }

    /**
     * 绘制五线谱的乐谱信息
     *
     * @param attributess
     * @param canvas
     * @param isDraw      是否绘制
     */
    private void drawStaffLines(Attributess attributess, Canvas canvas, boolean isDraw) {
        mFristStaffWidth = mLinsRoomWidth2 - moveLenth;
        mScendStaffWidth = mFristStaffWidth;
        if (isDraw) {
            if (isTowStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
            } else {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
            }
        }
        //绘制音符
        drawSign(canvas, attributess.getClefList(), isDraw);
        float wigth = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth = wigth;
        mScendStaffWidth = wigth;
        //绘制节拍
        drawTimes(canvas, attributess.getTime(), isDraw);
        mFristStaffWidth = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth += mSpeedLenth;
        mScendStaffWidth = mFristStaffWidth;
    }

    /**
     * 拍子
     *
     * @param canvas
     * @param time
     * @param isDraw
     */
    private void drawTimes(Canvas canvas, AttributessTime time, boolean isDraw) {
        if (isDraw) {
            drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_up - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_threeLins_up);
            drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_up - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_up);
            if (isTowStaff) {
                drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_down - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_threeLins_down);
                drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_down - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_down);
            }
        }
        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth;
    }

    /**
     * @param canvas
     * @param beat
     * @param left
     * @param rifht
     */
    private void drawTime(Canvas canvas, String beat, float left, int top, float rifht, int bottom) {
        switch (beat) {
            case "2":
                twoDrawable.setBounds((int) left, top, (int) rifht, bottom);
                twoDrawable.draw(canvas);
                break;
            case "3":
                threeDrawable.setBounds((int) left, top, (int) rifht, bottom);
                threeDrawable.draw(canvas);
                break;
            case "4":
                fourDrawable.setBounds((int) left, top, (int) rifht, bottom);
                fourDrawable.draw(canvas);
                break;
            case "6":
                sixDrawable.setBounds((int) left, top, (int) rifht, bottom);
                sixDrawable.draw(canvas);
                break;
            case "8":
                eightDrawable.setBounds((int) left, top, (int) rifht, bottom);
                eightDrawable.draw(canvas);
                break;
            case "9":
                nineDrawable.setBounds((int) left, top, (int) rifht, bottom);
                nineDrawable.draw(canvas);
                break;
            case "12":
                twelveDrawable.setBounds((int) left, top, (int) rifht, bottom);
                twelveDrawable.draw(canvas);
                break;
        }

    }

    /**
     * 绘制高音，低音，音调
     *
     * @param canvas
     * @param clefList
     * @param isDraw
     */
    private void drawSign(Canvas canvas, List<Clef> clefList, boolean isDraw) {
        mFristStaffWidth += mLinsRoomWidth2;
        mScendStaffWidth = mFristStaffWidth;
//        MyLogUtils.e(TAG, "mFristStaffWidth:" + mFristStaffWidth);
        //两条五线谱
        for (int j = 0; j < clefList.size(); j++) {
            if (j == 0) {
                switch (clefList.get(j).getSign()) {
                    case "G":
                        //高音
                        if (isDraw)
                            drawTreble(canvas, mFristStaffWidth, mLayoutCenterHeight - mLinsRoomWidth2 - mTrebleHeight,
                                    mFristStaffWidth + mTrebleWidth, mLayoutCenterHeight - mLinsRoomWidth2);
                        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth;
                        drawFifths(canvas, true, true, isDraw);
                        break;
                    case "F":
                        if (isDraw)
                            drawBass(canvas, mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth - mBassHeight,
                                    mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_up - mLinsRoomWidth);
                        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth;
                        drawFifths(canvas, true, false, isDraw);
                        break;
                }
            } else {
                switch (clefList.get(j).getSign()) {
                    case "G":
                        //高音
                        if (isDraw)
                            drawTreble(canvas, mScendStaffWidth, mLayoutCenterHeight + mLinsRoomWidth3,
                                    mScendStaffWidth + mTrebleWidth, mLayoutCenterHeight + mLinsRoomWidth3 + mTrebleHeight);
                        mScendStaffWidth += mTrebleWidth + mLinsRoomWidth;
                        drawFifths(canvas, false, true, isDraw);
                        break;
                    case "F":
                        //低音
                        if (isDraw)
                            drawBass(canvas, (int) mScendStaffWidth, mLayoutCenterHeight + mLinsRoomWidth4,
                                    (int) mScendStaffWidth + mBassWidth, mLayoutCenterHeight + mLinsRoomWidth4 + mBassHeight);
                        mScendStaffWidth += mTrebleWidth + mLinsRoomWidth;
                        drawFifths(canvas, false, false, isDraw);
                        break;
                }
            }
        }
    }

    /**
     * 绘制低音符号
     *
     * @param canvas
     * @param left
     * @param top
     * @param rifht
     * @param bottom
     */
    private void drawBass(Canvas canvas, float left, int top, float rifht, int bottom) {
        bassDrawable.setBounds((int) left, top, (int) rifht, bottom);
        bassDrawable.draw(canvas);

    }

    /**
     * 绘制高音符号
     *
     * @param canvas
     * @param left
     * @param top
     * @param rifht
     * @param bottom
     */
    private void drawTreble(Canvas canvas, float left, int top, float rifht, int bottom) {
        trebleDrawable.setBounds((int) left, top, (int) rifht, bottom);
        trebleDrawable.draw(canvas);
    }

    /**
     * 绘制升降调
     *
     * @param canvas
     * @param isfrist 是否是第一条五线谱
     * @param isG     是否是高音
     * @param isDraw
     */
    private void drawFifths(Canvas canvas, boolean isfrist, boolean isG, boolean isDraw) {
//        MyLogUtils.e(TAG, "mFristStaffWidth:" + mFristStaffWidth);
        if (fifth == null || fifth.length == 0) {
            return;
        }
        int size = fifth.length;
        if (!isDraw) {
            if (isfrist) {
                mFristStaffWidth += mLinsRoomWidth * size;
            } else {
                mScendStaffWidth += mLinsRoomWidth * size;
            }
            return;
        }
        int position = 0;
        if (isfrist) {
            position = mLayoutCenterHeight - mLinsRoomWidth4;
        } else {
            position = mLayoutCenterHeight + mLinsRoomWidth8;
        }
        List<Integer> list = new ArrayList<>();
        //升调
        if (isUpfifth) {
            if (isG) {
                //高音,升调
                list.addAll(HighRise);
//                MyLogUtils.e(TAG, "HighRise");
            } else {
                //低音，升调
                list.addAll(LowRise);
//                MyLogUtils.e(TAG, "LowRise");
            }
        } else {
            //降调
            if (isG) {
                //高音,降调
                list.addAll(HighDrop);
//                MyLogUtils.e(TAG, "HighDrop");
            } else {
                //低音，降调
                list.addAll(LowDrop);
//                MyLogUtils.e(TAG, "LowDrop");
            }
        }
        while (size != 0) {
            int where = position + list.get(0);
            if (isfrist) {
                //第一条五线谱
                if (isUpfifth) {
                    drawUpTune(canvas, mFristStaffWidth, where);
                } else {
                    drawDownTune(canvas, mFristStaffWidth, where);
                }
            } else {
                if (isUpfifth) {
                    drawUpTune(canvas, mScendStaffWidth, where);
                } else {
                    drawDownTune(canvas, mScendStaffWidth, where);
                }
            }
            if (isfrist) {
                mFristStaffWidth += mLinsRoomWidth;
            } else {
                mScendStaffWidth += mLinsRoomWidth;
            }
            list.remove(0);
            size--;
        }
    }

    /**
     * 绘制降调(开头的)
     */
    private void drawDownTune(Canvas canvas, float x, int y) {
        canvas.drawLine(x, y, x, y + mLinsRoomWidth5 / 2, mLinsPaint2);
        mPath.moveTo(x, y + mLinsRoomWidth3 / 2);
        //二介
        mPath.quadTo(x + mLinsRoomWidth, y + mLinsRoomWidth2, x, y + mLinsRoomWidth5 / 2);
        //三介
//        mPath.cubicTo();
        canvas.drawPath(mPath, mTailPaint);
    }

    /**
     * 绘制升调（开头的）
     */
    private void drawUpTune(Canvas canvas, float x, int y) {
        canvas.drawLine(x, y, x + mLinsRoomWidth, y - mLinsRoomWidth / 2, mLinsPaint2);
        canvas.drawLine(x, y - mLinsRoomWidth, x + mLinsRoomWidth, y - mLinsRoomWidth3 / 2, mLinsPaint2);
        canvas.drawLine(x + mLinsRoomWidth / 4, y - mLinsRoomWidth7 / 4, x + mLinsRoomWidth / 4, y + mLinsRoomWidth / 2, mLinsPaint2);
        canvas.drawLine(x + mLinsRoomWidth3 / 4, y - mLinsRoomWidth2, x + mLinsRoomWidth3 / 4, y + mLinsRoomWidth / 4, mLinsPaint2);
    }

    /**
     * 绘制降调(音符)
     */
    private void drawDownTune(Canvas canvas, float X, float startY, float stopY) {
        float x = X + mLinsRoomWidth;
        //绘制一条竖线
        drawLins(canvas, x, startY, x, stopY);
        mPath.moveTo(x, startY + (stopY - startY) / 2);
        mPath.quadTo(x + mLinsRoomWidth, stopY - (stopY - startY) / 4, x, stopY);
        canvas.drawPath(mPath, mTailPaint);
        mDwon.add(new Dwon(x, startY, x, stopY, x,
                startY + (stopY - startY) / 2,
                x + mLinsRoomWidth,
                stopY - (stopY - startY) / 4,
                x,
                stopY));
    }

    /**
     * 绘制直线（宽度固定）
     */
    private void drawLins(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        canvas.drawLine(startX, startY, stopX, stopY, mLinsPaint);
    }


    /**
     * 绘制五线谱符号的尾巴（八分音符的斜尾巴）
     *
     * @param canvas
     * @param x
     * @param y
     * @param isDwon true：符尾向下
     * @param type
     */
    private void drawTial(Canvas canvas, float x, int y, boolean isDwon, String type, Notes notes) {
        switch (type) {
            case "eighth":
                if (isDwon) {
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                } else {
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth);
                }
                break;
            case "16th":
                if (isDwon) {
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                } else {
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                }
                break;
            case "32th":
                if (isDwon) {
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth6,
                            x + mLinsRoomWidth, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth4);
                } else {
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth3);
                }
                break;
            case "64th":
                if (isDwon) {
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth6,
                            x + mLinsRoomWidth, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth4);
                    drawTials(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth7,
                            x + mLinsRoomWidth, y + mLinsRoomWidth6,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth6,
                            x + mLinsRoomWidth2, y + mLinsRoomWidth5);
                } else {
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth3,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth5,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth4,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth3);
                    drawTials(canvas, x + mLinsRoomWidth2, y - mLinsRoomWidth6,
                            x + mLinsRoomWidth2, y - mLinsRoomWidth5,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth5,
                            x + mLinsRoomWidth3, y - mLinsRoomWidth4);
                }
                break;
        }
    }

    //绘制符尾
    private void drawTials(Canvas canvas, float x, int y, float x1, int y1, float x2, int y2, float x3, int y3) {
        mPath.moveTo(x, y);
        mPath.cubicTo(x1, y1, x2, y2, x3, y3);
        canvas.drawPath(mPath, mTailPaint);
        mTial.add(new Tial(x, y, x1, y1, x2, y2, x3, y3));
    }

    /**
     * 绘制尾部连音
     *
     * @param beam
     * @param isDwon
     * @param tialX
     * @param tialStartY
     * @param tialStopY
     * @param canvas
     * @param chord
     */
    private void drawLegato(List<Beam> beam, boolean isDwon, float tialX, int tialStartY, int tialStopY, Canvas canvas, boolean chord) {
        //第一个开始绘制连音符的音符坐标
        float startX = 0;
        int startY = 0;
        float stopX = 0;
        int stopY = 0;
        //大于三个音符的时候相同的Y值
        int myY = 0;
        int dwon = isDwon ? -1 : 1;
        int hook = 1;
        for (int i = 0; i < beam.size(); i++) {
            Beam beam1 = beam.get(i);
            switch (beam1.getBeam()) {
                case "begin":
                    List<Legato> list;
                    if (legatosMap.get(beam1.getNumber()) != null) {
                        int y = legatosMap.get(beam1.getNumber()).get(0).getStopY();
                        legatosMap.get(beam1.getNumber()).get(0).setStopY(isDwon ? Math.max(y, tialStopY) : Math.min(y, tialStopY));
                    } else {
                        list = new ArrayList<>();
                        list.add(new Legato(tialX, tialStartY, tialX, tialStopY));
                        legatosMap.put(beam1.getNumber(), list);
                    }
                    break;
                case "end":
                    if (chord) return;
                    if (legatosMap.size() == 0) {
                        forwardHook.clear();
                        break;
                    }
                    if (legatosMap.get(1) == null) break;
                    if (legatosMap.get(1).size() == 0) break;
                    if (legatosMap.get(1).get(0) == null) break;
                    startX = legatosMap.get(1).get(0).getStartX();
                    startY = legatosMap.get(1).get(0).getStartY();
                    stopX = legatosMap.get(1).get(0).getStopX();
                    stopY = legatosMap.get(1).get(0).getStopY();
                    if (beam1.getNumber() == 1) {
                        if (legatosMap.get(1).size() == 1) {
////                            只有两个音符
                            if (legatosMap.size() == 1 && forwardHook.size() > 0) {
                                //只有一条连音线,处理hook
                                for (int j = 0; j < forwardHook.size(); j++) {
                                    canvas.drawLine(forwardHook.get(j).getStartX(),
                                            startY + dwon * (j + 1) * mLinsRoomWidth,
                                            forwardHook.get(0).getStopX(),
                                            startY + dwon * (j + 1) * mLinsRoomWidth, mBeamPaint);
                                    mSlurLins.add(new StaffSaveData(forwardHook.get(j).getStartX(),
                                            startY + dwon * (j + 1) * mLinsRoomWidth,
                                            forwardHook.get(0).getStopX(),
                                            startY + dwon * (j + 1) * mLinsRoomWidth));
                                }
                            }
                            drawLins(canvas, startX, startY, stopX, stopY);
                            drawLins(canvas, tialX, tialStartY, tialX, tialStopY);
                            mLins.add(new StaffSaveData(startX, startY, stopX, stopY));
                            mLins.add(new StaffSaveData(tialX, tialStartY, tialX, tialStopY));
                            //绘制底部连音线
                            for (int j = 0; j < legatosMap.size(); j++) {
                                canvas.drawLine(stopX, stopY + dwon * j * mLinsRoomWidth, tialX, tialStopY + dwon * j * mLinsRoomWidth, mBeamPaint);
                                mSlurLins.add(new StaffSaveData(stopX,
                                        stopY + dwon * j * mLinsRoomWidth,
                                        tialX,
                                        tialStopY + dwon * j * mLinsRoomWidth));
                            }
                        } else {
                            //三个以上音符
                            myY = isDwon ? Math.max(stopY, tialStopY) : Math.min(stopY, tialStopY);
                            if (legatosMap.size() == 1 && forwardHook.size() > 0) {
                                //只有一条连音线,处理hook
                                for (int j = 0; j < forwardHook.size(); j++) {
                                    canvas.drawLine(forwardHook.get(j).getStartX(),
                                            myY + dwon * (j + 1) * mLinsRoomWidth,
                                            forwardHook.get(0).getStopX(),
                                            myY + dwon * (j + 1) * mLinsRoomWidth, mBeamPaint);
                                    mSlurLins.add(new StaffSaveData(forwardHook.get(j).getStartX(),
                                            myY + dwon * (j + 1) * mLinsRoomWidth,
                                            forwardHook.get(0).getStopX(),
                                            myY + dwon * (j + 1) * mLinsRoomWidth));
                                }
                            }
////                            绘制符尾
                            for (int j = 0; j < legatosMap.get(1).size(); j++) {
                                drawLins(canvas, legatosMap.get(1).get(j).getStartX(),
                                        legatosMap.get(1).get(j).getStartY(),
                                        legatosMap.get(1).get(j).getStopX(), myY);
                                mLins.add(new StaffSaveData(legatosMap.get(1).get(j).getStartX(),
                                        legatosMap.get(1).get(j).getStartY(),
                                        legatosMap.get(1).get(j).getStopX(), myY));
                            }
                            drawLins(canvas, tialX, tialStartY, tialX, myY);
                            mLins.add(new StaffSaveData(tialX, tialStartY, tialX, myY));
//
//                            //绘制底部连音线
                            canvas.drawLine(stopX, myY, tialX, myY, mBeamPaint);
                            mSlurLins.add(new StaffSaveData(stopX, myY, tialX, myY));
                            for (int j = 1; j < legatosMap.size(); j++) {
                                if (legatosMap.get(j).size() == 1) {
                                    canvas.drawLine(legatosMap.get(j).get(0).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth,
                                            tialX,
                                            myY + dwon * (j) * mLinsRoomWidth, mBeamPaint);
                                    mSlurLins.add(new StaffSaveData(legatosMap.get(j).get(0).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth,
                                            tialX,
                                            myY + dwon * (j) * mLinsRoomWidth));
                                } else {
                                    canvas.drawLine(legatosMap.get(j).get(0).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth,
                                            legatosMap.get(j).get(1).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth, mBeamPaint);
                                    mSlurLins.add(new StaffSaveData(legatosMap.get(j).get(0).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth,
                                            legatosMap.get(j).get(1).getStopX(),
                                            myY + dwon * (j) * mLinsRoomWidth));
                                }
                            }
                        }
                        legatosMap.clear();
                        forwardHook.clear();
                    } else {
                        //只有三个音符
                        List<Legato> lists = legatosMap.get(beam1.getNumber());
                        if (lists == null) break;
                        lists.add(new Legato(tialX, tialStartY, tialX, tialStopY));
                        legatosMap.put(beam1.getNumber(), lists);
                    }
                    break;
                case "forward hook":
                    //第一个音符（向后）
                    forwardHook.add(new Legato(tialX, 0, tialX + mLinsRoomWidth, 0));
                    break;
                case "backward hook":
                    //最后一个音符（向前）
                    if (myY != 0) {
                        canvas.drawLine(tialX - mLinsRoomWidth,
                                myY + dwon * (hook) * mLinsRoomWidth,
                                tialX,
                                myY + dwon * (hook) * mLinsRoomWidth, mBeamPaint);
                        mSlurLins.add(new StaffSaveData(tialX - mLinsRoomWidth,
                                myY + dwon * (hook) * mLinsRoomWidth,
                                tialX,
                                myY + dwon * (hook) * mLinsRoomWidth));
                    } else {
                        canvas.drawLine(tialX - mLinsRoomWidth,
                                tialStopY + dwon * (hook) * mLinsRoomWidth,
                                tialX,
                                tialStopY + dwon * (hook) * mLinsRoomWidth, mBeamPaint);
                        mSlurLins.add(new StaffSaveData(tialX - mLinsRoomWidth,
                                tialStopY + dwon * (hook) * mLinsRoomWidth,
                                tialX,
                                tialStopY + dwon * (hook) * mLinsRoomWidth));
                    }
                    hook++;
                    break;
                case "continue":
                    if (beam1.getNumber() == 1) {
                        //只有num为1的时候保存数据
                        if (legatosMap.size() > 0) {
                            if (isDwon) {
                                legatosMap.get(1).get(0).setStopY(Math.max(legatosMap.get(1).get(0).getStopY(), tialStopY));
                            } else {
                                legatosMap.get(1).get(0).setStopY(Math.min(legatosMap.get(1).get(0).getStopY(), tialStopY));
                            }
                        }
                        if (chord) {
//                            和音的话叠加到最后一个元素
                            int y = legatosMap.get(1).get(legatosMap.get(1).size() - 1).getStopY();
                            legatosMap.get(1).get(legatosMap.get(1).size() - 1).setStopY(isDwon ? Math.max(y, tialStopY) : Math.min(y, tialStopY));
                        } else {
                            List<Legato> adds = legatosMap.get(1);
                            if (adds == null) break;
                            adds.add(new Legato(tialX, tialStartY, tialX, tialStopY));
                            legatosMap.put(beam1.getNumber(), adds);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 全音符符头
     *
     * @param isAddDot     是否添加圆点
     * @param canvas
     * @param isFristStaff
     */
    private void drawAllHollowHeads(boolean isAddDot, Canvas canvas, float x, float y, boolean isFristStaff) {
        float CenterX = x + mLinsRoomWidth3 / 2;
        float CenterY = y + mLinsRoomWidth / 2;
        canvas.save();
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 2, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 2);
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.rotate(30, CenterX, CenterY);
        RectF rectF = new RectF(CenterX - mLinsRoomWidth / 2, CenterY - mLinsRoomWidth / 4, CenterX + mLinsRoomWidth / 2, CenterY + mLinsRoomWidth / 4);
        canvas.drawOval(rectF, mWhitePaint);
        mWholeHead.add(new HeadData(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 2, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 2,
                CenterX, CenterY,
                CenterX - mLinsRoomWidth / 2, CenterY - mLinsRoomWidth / 4, CenterX + mLinsRoomWidth / 2, CenterY + mLinsRoomWidth / 4
        ));
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
            mDot.add(new Dot(CenterX + mLinsRoomWidth3 / 2, CenterY));
        }
        drawStub(canvas, x, CenterY, isFristStaff);
    }

    /**
     * 绘制符头短线
     *
     * @param canvas
     * @param x
     * @param y
     * @param isFristStaff
     */
    private void drawStub(Canvas canvas, float x, float y, boolean isFristStaff) {
        if (isFristStaff) {
            if (y < twoStaff_fiveLins_up) {
                drawStubLins(canvas, x, (int) (twoStaff_fiveLins_up - y) / mLinsRoomWidth, twoStaff_fiveLins_up, -1);
            } else if (twoStaff_fristLins_up < y) {
                drawStubLins(canvas, x, (int) (y - twoStaff_fristLins_up) / mLinsRoomWidth, twoStaff_fristLins_up, 1);
            }
        } else {
            if (y < twoStaff_fiveLins_down) {
                drawStubLins(canvas, x, (int) (twoStaff_fiveLins_down - y) / mLinsRoomWidth, twoStaff_fiveLins_down, -1);
            } else if (twoStaff_fristLins_down < y) {
                drawStubLins(canvas, x, (int) (y - twoStaff_fristLins_down) / mLinsRoomWidth, twoStaff_fristLins_down, 1);
            }
        }
    }

    /**
     * @param canvas
     * @param isReduce true:减
     */
    private void drawStubLins(Canvas canvas, float x, int num, float y, int isReduce) {
        if (num == 0) return;
        while (num != 0) {
            drawLins(canvas, x, y + isReduce * num * mLinsRoomWidth, x + mLinsRoomWidth3, y + isReduce * num * mLinsRoomWidth);
            mHeadLins.add(new StaffSaveData(x, (int) y + isReduce * num * mLinsRoomWidth, x + mLinsRoomWidth3, (int) y + isReduce * num * mLinsRoomWidth));
            num--;
        }
    }

    /**
     * 二分音符的符头
     *
     * @param isAddDot
     * @param canvas
     * @param isFristStaff
     */
    private void drawTowHollowHeads(boolean isAddDot, Canvas canvas, float x, float y, boolean isFristStaff) {
        float CenterX = x + mLinsRoomWidth3 / 2;
        float CenterY = y + mLinsRoomWidth / 2;
        canvas.save();
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 3, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 3);
        canvas.rotate(-30, CenterX, CenterY);
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.restore();
        canvas.save();
        RectF rectF = new RectF(CenterX - mLinsRoomWidth / 2, CenterY - mLinsRoomWidth / 4, CenterX + mLinsRoomWidth / 2, CenterY + mLinsRoomWidth / 4);
        canvas.rotate(-30, CenterX, CenterY);
        canvas.drawOval(rectF, mWhitePaint);
        canvas.restore();
        mHalfHead.add(new HeadData(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 3, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 3,
                CenterX, CenterY,
                CenterX - mLinsRoomWidth / 2, CenterY - mLinsRoomWidth / 4, CenterX + mLinsRoomWidth / 2, CenterY + mLinsRoomWidth / 4
        ));
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
            mDot.add(new Dot(CenterX + mLinsRoomWidth3 / 2, CenterY));
        }
        drawStub(canvas, x, CenterY, isFristStaff);
    }

    /**
     * 绘制纯黑色符头
     * 该符头主要是四分音符以后的符头
     *
     * @param isAddDot
     * @param canvas
     * @param x            绘制符头的中心左上x坐标
     * @param y            绘制符头的左上y坐标
     * @param isFristStaff
     */
    private void drawBlackHeads(boolean isAddDot, Canvas canvas, float x, float y, boolean isFristStaff) {
        float CenterX = x + mLinsRoomWidth3 / 2;
        float CenterY = y + mLinsRoomWidth / 2;
        canvas.save();
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth2 / 5, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth2 / 5);
        canvas.rotate(-30, CenterX, CenterY);//向上旋转30度
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.restore();
        mHead.add(new HeadData(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth2 / 5, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth2 / 5,
                CenterX, CenterY));
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
            mDot.add(new Dot(CenterX + mLinsRoomWidth3 / 2, CenterY));
        }
        drawStub(canvas, x, CenterY, isFristStaff);
    }

    /**
     * 设置绘制五线谱的数据
     *
     * @param iPlay
     */
    public void setStaffData(final IPlay iPlay) {
        MyLogUtils.e(TAG, "初始化五线谱");
        init();
        initStaffData();
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                iPlay.ReadyFinish();
            }
        });
    }

    /**
     * 播放/暂停
     */
    public void play(boolean isplay) {
        isMove = isplay;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (isMove) {
            thread = new MysurfaceviewThread();
            thread.start();
        }
    }

    /**
     * 初始化五线谱数据
     */
    private void initStaffData() {
        mFristStaffWidth = 0;
        mScendStaffWidth = 0;
        //是否绘制符尾（八分音符以后的倾斜符尾）
        isDrawTial = false;
        if (fristSingLenth != null) fristSingLenth.clear();
        //是否保存五线谱移动的数据
        isSaveData = true;
        moveLenth = 0;
        mProgressLeft = 0;

        //true：二条五线谱上
        isTowStaff = StaffDataHelper.getInstence().isTowStaff();
        mFristStaffData = StaffDataHelper.getInstence().getmFristStaffData();
        mSecondStaffData = StaffDataHelper.getInstence().getmSecondStaffData();
        mBackUPData = StaffDataHelper.getInstence().getmBackUPData();
        mAttributess = StaffDataHelper.getInstence().getmAttributess();
        DEFAULT_TIME_NUM = StaffDataHelper.getInstence().getDEFAULT_TIME_NUM();
        //保存整个谱子升降音的数组
        fifth = StaffDataHelper.getInstence().getFifth();
        measureDurationNum = StaffDataHelper.getInstence().getMeasureDurationNum();
        mSpeedTime = StaffDataHelper.getInstence().getmSpeedTime();
        mSpeedLenth = StaffDataHelper.getInstence().getmSpeedLenth();
        isUpfifth = StaffDataHelper.getInstence().isUpfifth();
        divisions = StaffDataHelper.getInstence().getDivisions();
        beats = StaffDataHelper.getInstence().getBeats();
        mLenth = mSpeedLenth * Float.valueOf(mAttributess.getDivisions()) / 6;
    }

    public void onResume() {
        isMove = true;
    }

    public void onPause() {
        isMove = false;
    }

    public void resetPullView() {
        isMove = false;
        isUpfifth = false;

        isSaveData = false;
        moveLenth = 0;
        mProgressLeft = 0;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }


    /**
     * 销毁时调用
     */
    public void onDrestry() {
        if (mCanvas != null) {
            holder.unlockCanvasAndPost(mCanvas);
            mCanvas = null;
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void endRefreshCanvas(final long millis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SurfaceHolder surfaceHolder = holder;
                synchronized (surfaceHolder) {
                    //锁定canvas
                    try {
                        Canvas canvas = surfaceHolder.lockCanvas();
                        //canvas 执行一系列画的动作
                        if (canvas != null) {
                            canvas.drawColor(Color.WHITE);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    /**
     * 获取速度
     *
     * @return
     */
    public String getmReta() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }

    /**
     * 加速
     */
    public String accelerate() {
        mReta += 0.1f;
        if (mReta >= 1.5f) mReta = 1.5f;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }

    /**
     * 减速
     */
    public String deceleration() {
        mReta -= 0.1f;
        if (mReta <= 0.5f) mReta = 0.5f;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mReta);
    }
}
