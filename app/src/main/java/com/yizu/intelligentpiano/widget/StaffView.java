package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Legato;
import com.yizu.intelligentpiano.bean.ReverseTie;
import com.yizu.intelligentpiano.bean.RiseRecord;
import com.yizu.intelligentpiano.bean.Tie;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.AttributessTime;
import com.yizu.intelligentpiano.bean.xml.Beam;
import com.yizu.intelligentpiano.bean.xml.Clef;
import com.yizu.intelligentpiano.bean.xml.Measure;
import com.yizu.intelligentpiano.bean.xml.MeasureBase;
import com.yizu.intelligentpiano.bean.xml.Notes;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by liuxiaozhu on 2017/10/11.
 * All Rights Reserved by YiZu
 * 五线谱
 */

public class StaffView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "StaffView";

    private Context mContext;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mLayoutCenterHeight;
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
    private Paint mRedPaint;
    private Paint mBeamPaint;

    private Paint mPathPaint;
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
    private int mLinsRoomWidth9;
    private int mLinsRoomWidth30;

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
    private List<Tie> mFristTie = new ArrayList<>();
    private List<Tie> mSecondTie = new ArrayList<>();

    private List<Tie> mFristSlur = new ArrayList<>();
    private List<Tie> mSecondSlur = new ArrayList<>();


    //保存第一个音符所有短连音符（向后）
    private int forwardHook = 0;
    //保存最后一个音符所有短连音符（向前）
    private int backwardHook = 0;

    //保存连音符的数据
    private Map<Integer, List<Legato>> legatosMap = new HashMap<>();

    //每小节多少Duration
    private int measureDurationNum = 0;
    //默认每个druction的长度20像素
    private int mSpeedLenth = 20;

    SurfaceHolder holder;
    private boolean isUpfifth = false;
    //保存整个谱子升降音的数组
    private String[] fifth;

    private List<Integer> HighRise;//高升
    private List<Integer> HighDrop;//高降
    private List<Integer> LowRise;//低升
    private List<Integer> LowDrop;//低降

    private Double[] typeNum = {1.0, 1.0 / 2, 1.0 / 4, 1.0 / 8, 1.0 / 16, 1.0 / 32, 1.0 / 64};
    private String[] typeString = {"whole", "half", "quarter", "eighth", "16th", "32th", "64th"};
    int divisions = 0;
    int beats = 0;
    private Canvas mCanvas;
    private MysurfaceviewThread thread;

    /**
     * ********************换成3行需要的参数*********************************
     */
    //每一行显示的小节数
    private int measureNum = 0;
    //每一行开始的位置（X）
    private int start_x = 0;
    //当前行号
    private int mLineNum = 0;
    private int index = 0;
    private boolean isFrist = false;
    private ProgresView progresView;
    private IPlay iPlay;
    //    进度条第一行开始的位置
    private float x = 0;
    //保存第一行每一小节结束的位置
    private List<Float> fristLinsEndX = new ArrayList();
    private List<RiseRecord> riseRecords = new ArrayList<>();
    //默认两条五线谱
    private boolean isTwoStaff = true;
    //一条五线谱的时候是高音
    private boolean isUpNote = true;

    private boolean isStopBeam = false;
    //反向的Tie
    private ReverseTie reverseTie;
    private boolean isSlur = false;


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
        //6个五线谱，每个五线谱4隔间
        mLinsRoomWidth = mLayoutHeight / (30 * Constents.LINE_NUM);
        mLinsRoomWidth2 = mLinsRoomWidth * 2;
        mLinsRoomWidth3 = mLinsRoomWidth * 3;
        mLinsRoomWidth4 = mLinsRoomWidth * 4;
        mLinsRoomWidth5 = mLinsRoomWidth * 5;
        mLinsRoomWidth6 = mLinsRoomWidth * 6;
        mLinsRoomWidth7 = mLinsRoomWidth * 7;
        mLinsRoomWidth8 = mLinsRoomWidth * 8;
        mLinsRoomWidth9 = mLinsRoomWidth * 9;
        mLinsRoomWidth30 = mLinsRoomWidth * 30;
        mLayoutCenterHeight = mLayoutHeight / (2 * Constents.LINE_NUM);
        twoStaff_fristLins_up = mLayoutCenterHeight - mLinsRoomWidth5;
        twoStaff_threeLins_up = mLayoutCenterHeight - mLinsRoomWidth7;
        twoStaff_fiveLins_up = mLayoutCenterHeight - mLinsRoomWidth9;
        twoStaff_fristLins_down = mLayoutCenterHeight + mLinsRoomWidth9;
        twoStaff_threeLins_down = mLayoutCenterHeight + mLinsRoomWidth7;
        twoStaff_fiveLins_down = mLayoutCenterHeight + mLinsRoomWidth5;
        initSignScan();
        initRiseDrop();
        mBeamPaint.setStrokeWidth(mLinsRoomWidth / 3);
        mTailPaint.setStrokeWidth(mLinsRoomWidth / 3);
        mRedPaint.setStrokeWidth(mLinsRoomWidth / 3);
        mLinsPaint.setStrokeWidth(mLinsRoomWidth / 10);
        mPathPaint.setStrokeWidth(mLinsRoomWidth / 5);
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
        mBlackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(getResources().getColor(R.color.white));
        mWhitePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTailPaint = new Paint();
        mTailPaint.setAntiAlias(true);
        mTailPaint.setColor(getResources().getColor(R.color.black));
        mTailPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLinsPaint = new Paint();
//        mLinsPaint.setAntiAlias(true);
        mLinsPaint.setColor(getResources().getColor(R.color.black));
        mLinsPaint.setStyle(Paint.Style.STROKE);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(getResources().getColor(R.color.black));
        mPathPaint.setStyle(Paint.Style.STROKE);

        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setColor(getResources().getColor(R.color.red));
        mRedPaint.setStyle(Paint.Style.STROKE);

        mBeamPaint = new Paint();
        mBeamPaint.setAntiAlias(true);
        mBeamPaint.setColor(getResources().getColor(R.color.black));
        mBeamPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        init();
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
    }

    public void onDrestry() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }


    class MysurfaceviewThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            synchronized (holder) {
                //锁定canvas
                mCanvas = holder.lockCanvas();
                //canvas 执行一系列画的动作
                if (mCanvas != null) {
                    mCanvas.drawColor(Color.WHITE);
                    if (mAttributess != null) {
                        //绘制音符
                        drawSgin(mCanvas);
                    }
                }
                try {
                    //释放canvas对象，并发送到SurfaceView
                    holder.unlockCanvasAndPost(mCanvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isFrist) {
                    isFrist = false;
                    progresView.setProgressData(StaffView.this,
                            iPlay, mLinsRoomWidth,
                            x, fristLinsEndX, measureNum);
                }
            }
        }
    }

    /**
     * 绘制符号
     */
    private void drawSgin(Canvas canvas) {
        if (index >= mFristStaffData.size()) return;
        int size = Math.min(mFristStaffData.size(), index + measureNum * Constents.LINE_NUM);
        for (int j = index; j < size; j++) {
            mLineNum = (j - index) / measureNum;
            if (j % measureNum == 0) {
                //初始化五线谱(条数)
                drawStaffLines(mAttributess, mCanvas);
                if (isFrist && mLineNum == 0) {
                    x = mFristStaffWidth;
                }
                //清除所有数据
                mFristTie.clear();
                mSecondTie.clear();
                mFristSlur.clear();
                mSecondSlur.clear();
                reverseTie = null;
            }
            riseRecords.clear();
            List<MeasureBase> base = mFristStaffData.get(j).getMeasure();
            int baseSize = base.size();
            if (isUpNote) {
                //第一个五线谱
                legatosMap.clear();
                forwardHook = 0;
                backwardHook = 0;
                isStopBeam = false;
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
            } else {
                //第一个五线谱
                legatosMap.clear();
                forwardHook = 0;
                backwardHook = 0;
                isStopBeam = false;
                for (int k = 0; k < baseSize; k++) {
                    Notes nots = base.get(k).getNotes();
                    if (nots != null) {
                        if (k + 1 < base.size()) {
                            drawNotes(canvas, nots, false, base.get(k + 1).getNotes());
                        } else {
                            drawNotes(canvas, nots, false, null);
                        }
                    }
                }
                mFristStaffWidth = mScendStaffWidth;
            }
            if (isTwoStaff) {
                legatosMap.clear();
                forwardHook = 0;
                backwardHook = 0;
                isStopBeam = false;
                List<MeasureBase> base1 = mSecondStaffData.get(j).getMeasure();
                int base1Size = base1.size();
                mScendStaffWidth = mFristStaffWidth - mBackUPData.get(j) * mSpeedLenth;
                riseRecords.clear();
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
            if (isTwoStaff) {
                mFristStaffWidth = Math.max(mFristStaffWidth, mScendStaffWidth) + mLinsRoomWidth2;
                mScendStaffWidth = mFristStaffWidth;
            } else {
                mFristStaffWidth += mLinsRoomWidth2;
                if (!isUpNote) {
                    mScendStaffWidth = mFristStaffWidth;
                }
            }
            if (isFrist && mLineNum == 0) {
                fristLinsEndX.add(mFristStaffWidth - mLinsRoomWidth2);
            }
            drawMeasureLins(canvas, (j + 1) % measureNum != 0 && j + 1 != mFristStaffData.size());
            if ((j + 1) % measureNum != 0 && j + 1 != mFristStaffData.size()) {
                mFristStaffWidth += mLinsRoomWidth * 2;
                if (!isUpNote) {
                    mScendStaffWidth = mFristStaffWidth;
                }
                if (isTwoStaff) {
                    mScendStaffWidth += mFristStaffWidth;
                }
            } else {
                drawStaffLins(canvas);
            }
        }
    }

    /**
     * 绘制间线
     *
     * @param canvas
     */
    private void drawStaffLins(Canvas canvas) {
        //第一条五线谱的五条间线
        if (isUpNote) {
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
        } else {
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
        }
        if (isTwoStaff) {
            //第二条五线谱的五条间线
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 + start_x, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
        }
    }

    /**
     * 绘制小节线
     */

    private void drawMeasureLins(Canvas canvas, boolean noIsLast) {
        if (noIsLast) {
            //每小节的竖直分割线
            if (isUpNote) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
            } else {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            }
            if (isTwoStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            }
        } else {
            //如果是两条五线谱，绘制最后一小节的尾线
            if (isTwoStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            } else {
                if (isUpNote) {
                    canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
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
            isDrawTial = false;
            if (note != null) {
                if (notes.getStems().equals("up")) {
                    if (note.getChord()) {
                        isDrawTial = false;
                    } else {
                        isDrawTial = true;
                    }
                } else {
                    if (!notes.getChord() && note.getChord()) {
                        isDrawTial = true;
                    } else {
                        isDrawTial = false;
                    }
                }
            }
            //音符
            drawPitch(canvas, notes, isFristStaff, note == null || !note.getChord());
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
     * @param end
     * @param isFristStaff 第一条五线谱
     */
    private void drawPitch(Canvas canvas, Notes notes, boolean isFristStaff, boolean end) {
        //加点（延音）
        boolean isAddDot = notes.getDot();
        //符尾向下
        boolean isDwon = false;
        if (notes.getStems() != null) {
            isDwon = notes.getStems().equals("down");
        }
        float y = getPatchPosiotion(notes, isFristStaff) + mLineNum * mLinsRoomWidth30;
        String type = notes.getType();
        if (type == null) {
            type = getType(notes);
        }
        if (notes.getTie() != null) {
            if (isSlur) isSlur = false;
            //绘制弧形延音线
            drawTie(canvas, y, notes, isFristStaff, type);
        } else if (notes.getSlur() != null) {
            if (!isSlur) isSlur = true;
            //绘制红色弧形延音线
            drawSlur(canvas, y, notes, isFristStaff, type);
        }
        //绘制音符符头
        drawHeads(type, isAddDot, canvas, y, isFristStaff, notes);
        //符杠的X坐标
        float tialX = getTialX(isFristStaff, isDwon);
        float tialStartY = isDwon ? y + mLinsRoomWidth3 / 4 : y + mLinsRoomWidth / 4;
        float tialStopY = getTialY(type, isDwon, y);
        //初始化符尾连音符
        if (notes.getBeam() != null && !notes.getChord())
            initLegato(notes.getBeam(), tialX, tialStartY, tialStopY);
        if (notes.getChord() && legatosMap.size() != 0) {
            int index = legatosMap.get(1).size() - 1;
            float startY = legatosMap.get(1).get(index).getStartY();
            float stopY = legatosMap.get(1).get(index).getStopY();
            legatosMap.get(1).get(index)
                    .setStartY(isDwon ? Math.min(startY, tialStartY) : Math.max(startY, tialStartY));
            legatosMap.get(1).get(index)
                    .setStopY(isDwon ? Math.max(stopY, tialStopY) : Math.min(stopY, tialStopY));
        }
        if (legatosMap.size() != 0) {
            if (isStopBeam && end) drawLegato(isDwon, canvas);
        } else {
            drawLins(canvas, tialX, tialStartY, tialX, tialStopY);
            //绘制符号尾部（八分音符以后）
            if (isDrawTial) {
                drawTial(canvas, isFristStaff ? mFristStaffWidth : mScendStaffWidth, y, isDwon, type, notes);
            }
        }
        if (legatosMap.size() == 0 && reverseTie != null) {
            //绘制反向Tie
            drawReverseTie(canvas);
        }
    }

    /**
     * 绘制连音符
     *
     * @param isDwon
     * @param canvas
     */
    private void drawLegato(boolean isDwon, Canvas canvas) {
        isStopBeam = false;
        float max = 0f;
        int dwon = isDwon ? -1 : 1;
        boolean isTwo = legatosMap.get(1).size() == 2;
        float y1 = 0f;
        float x1 = 0f;
        float x2 = 0f;
        float y2 = 0f;
        if (!isTwo) {
            List<Legato> legato = legatosMap.get(1);
            for (int i = 0; i < legato.size(); i++) {
                float y = legato.get(i).getStopY();
                if (i == 0) {
                    max = y;
                } else {
                    max = isDwon ? Math.max(max, y) : Math.min(max, y);
                }
            }
            //绘制符杆
            for (int i = 0; i < legato.size(); i++) {
                float x = legato.get(i).getStartX();
                float starY = legato.get(i).getStartY();
                drawLins(canvas, x, starY, x, max);
            }
            //绘制连音线
            for (int i = 0; i < legatosMap.size(); i++) {
                float y = max + dwon * mLinsRoomWidth * i;
                canvas.drawLine(legatosMap.get(i + 1).get(0).getStartX(), y,
                        legatosMap.get(i + 1).get(legatosMap.get(i + 1).size() - 1).getStartX(), y,
                        mBeamPaint);
            }
            //绘制向后短线
            for (int i = 1; i <= forwardHook; i++) {
                canvas.drawLine(legato.get(0).getStartX(), max + dwon * i * mLinsRoomWidth,
                        legato.get(0).getStartX() + mLinsRoomWidth, max + dwon * i * mLinsRoomWidth,
                        mBeamPaint);
            }
            //绘制向前短线
            for (int i = 1; i <= backwardHook; i++) {
                canvas.drawLine(legato.get(legato.size() - 1).getStartX() - mLinsRoomWidth, max + dwon * i * mLinsRoomWidth,
                        legato.get(legato.size() - 1).getStartX(), max + dwon * i * mLinsRoomWidth,
                        mBeamPaint);
            }
            if (reverseTie != null) {
                reverseTie.setY2(max);
            }
        } else {
            List<Legato> legato = legatosMap.get(1);
            y1 = legato.get(0).getStopY();
            y2 = legato.get(1).getStopY();
            x1 = legato.get(0).getStartX();
            x2 = legato.get(1).getStartX();
            //绘制符杆
            drawLins(canvas, x1, legato.get(0).getStartY(), x1, y1);
            drawLins(canvas, x2, legato.get(1).getStartY(), x2, y2);
            //绘制连音线
            for (int i = 0; i < legatosMap.size(); i++) {
                canvas.drawLine(legatosMap.get(i + 1).get(0).getStartX(), y1 + dwon * mLinsRoomWidth * i,
                        legatosMap.get(i + 1).get(legatosMap.get(i + 1).size() - 1).getStartX(), y2 + dwon * mLinsRoomWidth * i,
                        mBeamPaint);
            }
            //绘制向后短线
//            float slope = ((y2 - y1) * (mLinsRoomWidth2 / 3)) / (x2 - x1);
            for (int i = 1; i <= forwardHook; i++) {
                canvas.drawLine(x1, y1 + dwon * i * mLinsRoomWidth,
                        x1 + mLinsRoomWidth, y1 + dwon * i * mLinsRoomWidth,
                        mBeamPaint);
            }
            //绘制向前短线
            for (int i = 1; i <= backwardHook; i++) {
                canvas.drawLine(x2 - mLinsRoomWidth, y2 + dwon * i * mLinsRoomWidth,
                        x2, y2 + dwon * i * mLinsRoomWidth,
                        mBeamPaint);
            }
            if (reverseTie != null) {
                if (reverseTie.getX2() == x2) {
                    reverseTie.setY2(y2);
                } else {
                    reverseTie.setY2(y1);
                }
            }
        }
        forwardHook = 0;
        backwardHook = 0;
        legatosMap.clear();
    }

    /**
     * 获取音符符尾尾坐标
     *
     * @param type
     * @param isDwon
     * @param y
     * @return
     */
    private float getTialY(String type, boolean isDwon, float y) {
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
            return X + mLinsRoomWidth2 - 2;
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
     * @param notes
     */
    private void drawHeads(String type, boolean isAddDot, Canvas canvas, float y, boolean isFristStaff, Notes notes) {
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
        if (notes.getPitch().getAlter() != null) {
            //绘制升降音
            drawfitts(canvas, x, y, notes.getPitch().getAlter(),
                    notes.getPitch().getStep(), notes.getPitch().getOctave());
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
    private void drawSlur(Canvas canvas, float i, Notes notes, boolean isFristStaff, String type) {
        Path mPath = new Path();
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
                    if (isFristStaff) {
                        mFristSlur.add(new Tie(x + mLinsRoomWidth, y, isUp));
                    } else {
                        mSecondSlur.add(new Tie(x + mLinsRoomWidth, y, isUp));
                    }
                    break;
                case "stop":
                    Tie ties = null;
                    if (isFristStaff) {
                        if (mFristSlur.size() == 0) return;
                        ties = mFristSlur.get(0);
                    } else {
                        if (mSecondSlur.size() == 0) return;
                        ties = mSecondSlur.get(0);
                    }
                    if (ties == null) return;
                    mPath.moveTo(ties.getX(), ties.getY());
                    if (ties.isUp() == isUp) {
                        mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth / 2, y), x - mLinsRoomWidth / 2, y);
                        canvas.drawPath(mPath, mRedPaint);
                    } else {
                        y = getTieY(isUp, y, type) + (isUp ? 1 : -1) * mLinsRoomWidth;
                        reverseTie = new ReverseTie(ties.getX(), ties.getY(), ties.isUp(),
                                x - mLinsRoomWidth, y, isUp);
                    }
                    if (isFristStaff) {
                        mFristSlur.remove(0);
                    } else {
                        mSecondSlur.remove(0);
                    }
                    mPath.reset();
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

    private void drawTie(Canvas canvas, float i, Notes notes, boolean isFristStaff, String type) {
        Path mPath = new Path();
        float x, y;
        if (isFristStaff) {
            x = mFristStaffWidth + mLinsRoomWidth;
        } else {
            x = mScendStaffWidth + mLinsRoomWidth;
        }
        boolean isUp;
        if (notes.getStems().equals("up")) {
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
                    if (isFristStaff) {
                        mFristTie.add(new Tie(x + mLinsRoomWidth, y, isUp));
                    } else {
                        mSecondTie.add(new Tie(x + mLinsRoomWidth, y, isUp));
                    }
                    break;
                case "stop":
                    Tie ties;
                    if (isFristStaff) {
                        if (mFristTie.size() == 0) return;
                        ties = mFristTie.get(0);
                    } else {
                        if (mSecondTie.size() == 0) return;
                        ties = mSecondTie.get(0);
                    }
                    if (ties == null) return;
                    mPath.moveTo(ties.getX(), ties.getY());
                    if (ties.isUp() == isUp) {
                        mPath.quadTo((ties.getX() + x) / 2, getTieZ(ties, x - mLinsRoomWidth / 2, y),
                                x - mLinsRoomWidth / 2, y);
                        canvas.drawPath(mPath, mPathPaint);
                    } else {
                        y = getTieY(isUp, y, type) + (isUp ? 1 : -1) * mLinsRoomWidth;
                        reverseTie = new ReverseTie(ties.getX(), ties.getY(), ties.isUp(),
                                x - mLinsRoomWidth, y, isUp);
                    }
                    if (isFristStaff) {
                        mFristTie.remove(0);
                    } else {
                        mSecondTie.remove(0);
                    }
                    break;
            }
        }
    }

    /**
     * 绘制方向不一样的弧线
     * @param canvas
     */
    private void drawReverseTie(Canvas canvas) {
        Path mPath = new Path();
        float x1 = reverseTie.getX1() - mLinsRoomWidth / 2;
        float y1 = reverseTie.getY1();
        float x2 = reverseTie.getX2() + mLinsRoomWidth;
        float y2 = reverseTie.getY2();
        mPath.moveTo(x1, y1);
        if (Math.abs(x1 - x2) / mLinsRoomWidth4 > Math.abs(y1 - y2) / mLinsRoomWidth) {
            mPath.quadTo((x1 + x2) / 2,
                    getTieZ(x1, x2 - mLinsRoomWidth, y2, reverseTie.isUp1()),
                    x2 - mLinsRoomWidth / 2, y2);
        } else {
            if (!reverseTie.isUp2()) {
                if (y1 < y2) {
                    mPath.quadTo(x1 + (x2 - x1) / 4,
                            y1 + (y2 - y1) * 3 / 4,
                            x2 - mLinsRoomWidth / 2, y2);
                } else {
                    mPath.quadTo(x1 + (x2 - x1) * 3 / 4,
                            y1 - (y1 - y2) / 4,
                            x2 - mLinsRoomWidth / 2, y2);
                }
            } else {
                if (y1 < y2) {
                    mPath.quadTo(x1 + (x2 - x1) * 3 / 4,
                            y1 + (y2 - y1) / 4,
                            x2 - mLinsRoomWidth / 2, y2);
                } else {
                    mPath.quadTo(x1 + (x2 - x1) / 4,
                            y1 - (y1 - y2) * 3 / 4,
                            x2 - mLinsRoomWidth / 2, y2);
                }
            }
        }
        canvas.drawPath(mPath, isSlur ? mRedPaint : mPathPaint);
        reverseTie = null;
    }

    //获取符杠的尾部
    private float getTieY(boolean isUp, float y, String type) {
        int up;
        if (isUp) {
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
                return y + mLinsRoomWidth4 * up;
            case "16th":
                return y + mLinsRoomWidth5 * up;
            case "32th":
                return y + mLinsRoomWidth6 * up;
            case "64th":
                return y + mLinsRoomWidth7 * up;
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

    private float getTieZ(float startX, float stopX, float y, boolean isUp) {
        float num = (stopX - startX) / mLinsRoomWidth4;
        if (num <= 0) {
            num = 1;
        } else if (num > 3) {
            num = 3;
        }
        if (isUp) {
            return y + mLinsRoomWidth * num;
        } else {
            return y - mLinsRoomWidth * num;
        }
    }

    /**
     * 绘制每个音符是否升降音
     */
    private void drawfitts(Canvas canvas, float x, float y, String alter, String step, String octave) {
        int alters = selectFitts(Integer.valueOf(alter), step, octave);
        if (alters == -5) return;
        switch (alters) {
            case -2:
                //重降 bb
                drawDownTune(canvas, x - mLinsRoomWidth, y - mLinsRoomWidth, y + mLinsRoomWidth);
                drawDownTune(canvas, x, y - mLinsRoomWidth, y + mLinsRoomWidth);
                break;
            case -1:
                //降 b
                drawDownTune(canvas, x, y - mLinsRoomWidth, y + mLinsRoomWidth);
                break;
            case 0:
//                还原
                drawReduction(canvas, x - mLinsRoomWidth / 2, y);
                break;
            case 1:
//                升 #
                drawRise(canvas, x - mLinsRoomWidth / 2, y, 1);
                break;
            case 2:
                //重升 x
                drawRise(canvas, x - mLinsRoomWidth / 2, y, 2);
                break;
        }
    }

    private int selectFitts(int alter, String step, String octave) {
        if (alter == 0) {
            Iterator iterator = riseRecords.iterator();
            while (iterator.hasNext()) {
                RiseRecord bean = (RiseRecord) iterator.next();
                if (bean.getOctave().equals(octave) && bean.getStep().equals(step)) {
                    iterator.remove();
                }
            }
            return 0;
        }
        if (alter > 0) {
            for (int i = 0; i < riseRecords.size(); i++) {
                RiseRecord bean = riseRecords.get(i);
                if (bean.getOctave().equals(octave) && bean.getStep().equals(step)) {
                    if (bean.getAlter() == alter) return -5;
                    if (bean.getAlter() == 1 && alter == 2) {
                        riseRecords.get(i).setAlter(2);
                        return 1;
                    }
                }
            }
            riseRecords.add(new RiseRecord(step, octave, alter));
            return alter;
        } else if (alter < 0) {
            for (int i = 0; i < riseRecords.size(); i++) {
                RiseRecord bean = riseRecords.get(i);
                if (bean.getOctave().equals(octave) && bean.getStep().equals(step)) {
                    if (bean.getAlter() == alter) return -5;
                    if (bean.getAlter() == -1 && alter == -2) {
                        riseRecords.get(i).setAlter(-2);
                        return -1;
                    }
                }
            }
            riseRecords.add(new RiseRecord(step, octave, alter));
            return alter;
        }
        return -5;
    }

    /**
     * 绘制升调
     */
    private void drawRise(Canvas canvas, float x, float y, int alter) {
        if (alter == 1) {
//            #
            canvas.drawLine(x, y + mLinsRoomWidth / 4,
                    x + mLinsRoomWidth, y, mPathPaint);
            canvas.drawLine(x, y + mLinsRoomWidth / 2,
                    x + mLinsRoomWidth, y + mLinsRoomWidth / 4, mPathPaint);
            //两条竖线
            canvas.drawLine(x + mLinsRoomWidth / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth / 4, y + mLinsRoomWidth3 / 4, mPathPaint);
            canvas.drawLine(x + mLinsRoomWidth3 / 4, y - mLinsRoomWidth / 3,
                    x + mLinsRoomWidth3 / 4, y + mLinsRoomWidth3 / 4, mPathPaint);
        } else {
            //x
            canvas.drawLine(x, y, x + mLinsRoomWidth, y + mLinsRoomWidth, mPathPaint);
            canvas.drawLine(x, y + mLinsRoomWidth, x + mLinsRoomWidth, y, mPathPaint);
        }
    }

    /**
     * 绘制还原符号
     */
    private void drawReduction(Canvas canvas, float x, float y) {
        canvas.drawLine(x, y - mLinsRoomWidth, x, y + mLinsRoomWidth, mPathPaint);
        canvas.drawLine(x + mLinsRoomWidth, y - mLinsRoomWidth / 2,
                x + mLinsRoomWidth, y + mLinsRoomWidth3 / 2, mPathPaint);
        canvas.drawLine(x, y, x + mLinsRoomWidth, y - mLinsRoomWidth / 2, mPathPaint);
        canvas.drawLine(x, y + mLinsRoomWidth,
                x + mLinsRoomWidth, y + mLinsRoomWidth / 2, mPathPaint);
    }

    /**
     * 获取音符的位置
     *
     * @param notes
     * @param isFristStaff
     */

    private float getPatchPosiotion(Notes notes, boolean isFristStaff) {
        int pa = Integer.parseInt(notes.getPitch().getOctave());
        float posiotion = 0;
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
        if (rest == null) return;
        Path mPath = new Path();
        //休止符
        switch (rest) {
            case "long":
                //4音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth,
                            twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mBeamPaint);
                } else {
                    canvas.drawLine(mFristStaffWidth,
                            twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mBeamPaint);
                }
                break;
            case "breve":
                //2音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth,
                            twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mBeamPaint);
                } else {
                    canvas.drawLine(mFristStaffWidth,
                            twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mBeamPaint);
                }
                break;
            case "whole":
                //1音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth,
                            twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mBeamPaint);
                } else {
                    canvas.drawLine(mFristStaffWidth,
                            twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mBeamPaint);
                }
                break;
            case "half":
                //二分休止符
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth3 / 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 2,
                            twoStaff_threeLins_down - mLinsRoomWidth3 / 2 + mLineNum * mLinsRoomWidth30, mBeamPaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth3 / 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 2,
                            twoStaff_threeLins_up - mLinsRoomWidth3 / 2 + mLineNum * mLinsRoomWidth30, mBeamPaint);
                }
                break;
            case "quarter":
                //四分休止符
                if (inTowStaffs) {
                    //第二条线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);

                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30, mBeamPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_fristLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth,
                            twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30, mBeamPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                }
                break;
            case "eighth":
                //八分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
            case "16th":
                //16分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
            case "32th":
                //32分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
            case "64th":
                //64分休止符
                if (inTowStaffs) {
                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 5 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 5 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
            case "128th":
                //128分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 6 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 6 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
            case "256th":
                //256分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 7 / 7, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 3 / 7 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 6 / 7, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 3 / 7 + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth * 2 + mLineNum * mLinsRoomWidth30,
                            mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth + mLineNum * mLinsRoomWidth30, mLinsPaint);
                }
                break;
        }
    }

    /**
     * 绘制五线谱的乐谱信息
     *
     * @param attributess
     * @param canvas
     */
    private void drawStaffLines(Attributess attributess, Canvas canvas) {
        mFristStaffWidth = mLinsRoomWidth2 + start_x;
        mScendStaffWidth = mFristStaffWidth;
        if (isTwoStaff) {
            canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
        } else {
            if (isUpNote) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30, mLinsPaint);
            } else {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30,
                        mFristStaffWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, mLinsPaint);
            }
        }
        //绘制音符
        drawSign(canvas, attributess.getClefList());
        float maxS = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth = maxS;
        mScendStaffWidth = mFristStaffWidth;
        //绘制节拍
        drawTimes(canvas, attributess.getTime());
        float max = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth = max + mLinsRoomWidth;
        mScendStaffWidth = mFristStaffWidth;
    }

    /**
     * 拍子
     *
     * @param canvas
     * @param time
     */
    private void drawTimes(Canvas canvas, AttributessTime time) {
        if (isUpNote) {
            drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_up - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_threeLins_up + mLineNum * mLinsRoomWidth30);
            drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_up - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30);
        } else {
            drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_down - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
            drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_down - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30);
            mFristStaffWidth += mTwelveWidth + mLinsRoomWidth;
        }
        //第二条
        if (isTwoStaff) {
            drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_down - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_threeLins_down + mLineNum * mLinsRoomWidth30);
            drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_down - mNumHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTwelveWidth, twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30);
        }
        mFristStaffWidth += mTwelveWidth + mLinsRoomWidth;
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
     */
    private void drawSign(Canvas canvas, List<Clef> clefList) {
        mFristStaffWidth += mLinsRoomWidth2;
        mScendStaffWidth = mFristStaffWidth;
        if (isUpNote) {
            //高音
            drawTreble(canvas, mFristStaffWidth, mLayoutCenterHeight - mLinsRoomWidth2 - mTrebleHeight + mLineNum * mLinsRoomWidth30,
                    mFristStaffWidth + mTrebleWidth, mLayoutCenterHeight - mLinsRoomWidth2 + mLineNum * mLinsRoomWidth30);
            mFristStaffWidth += mBassWidth + mLinsRoomWidth;
            drawFifths(canvas, true, true);
        } else {
            //低音
            drawBass(canvas, (int) mScendStaffWidth, mLayoutCenterHeight + mLinsRoomWidth4 + mLineNum * mLinsRoomWidth30,
                    (int) mScendStaffWidth + mBassWidth, mLayoutCenterHeight + mLinsRoomWidth4 + mBassHeight + mLineNum * mLinsRoomWidth30);
            mScendStaffWidth += mBassWidth + mLinsRoomWidth;
            drawFifths(canvas, false, false);
        }
        if (isTwoStaff) {
            //两条五线谱
            drawBass(canvas, (int) mScendStaffWidth, mLayoutCenterHeight + mLinsRoomWidth4 + mLineNum * mLinsRoomWidth30,
                    (int) mScendStaffWidth + mBassWidth, mLayoutCenterHeight + mLinsRoomWidth4 + mBassHeight + mLineNum * mLinsRoomWidth30);
            mScendStaffWidth += mBassWidth + mLinsRoomWidth;
            drawFifths(canvas, false, clefList.get(0).getSign().equals("G"));
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
     */
    private void drawFifths(Canvas canvas, boolean isfrist, boolean isG) {
        if (fifth == null || fifth.length == 0) {
            return;
        }
        int size = fifth.length;
        int position;
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
            } else {
                //低音，升调
                list.addAll(LowRise);
            }
        } else {
            //降调
            if (isG) {
                //高音,降调
                list.addAll(HighDrop);
            } else {
                //低音，降调
                list.addAll(LowDrop);
            }
        }
        while (size != 0) {
            int where = position + list.get(0) + mLineNum * mLinsRoomWidth30;
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
        Path mPath = new Path();
        canvas.drawLine(x, y, x, y + mLinsRoomWidth5 / 2, mPathPaint);
        mPath.moveTo(x, y + mLinsRoomWidth3 / 2);
        //二介
        mPath.quadTo(x + mLinsRoomWidth, y + mLinsRoomWidth2, x, y + mLinsRoomWidth5 / 2);
        //三介
//        mPath.cubicTo();
        canvas.drawPath(mPath, mLinsPaint);
    }

    /**
     * 绘制升调（开头的）
     */
    private void drawUpTune(Canvas canvas, float x, int y) {
        canvas.drawLine(x, y, x + mLinsRoomWidth, y - mLinsRoomWidth / 2, mPathPaint);
        canvas.drawLine(x, y - mLinsRoomWidth, x + mLinsRoomWidth, y - mLinsRoomWidth3 / 2, mPathPaint);
        canvas.drawLine(x + mLinsRoomWidth / 4, y - mLinsRoomWidth7 / 4, x + mLinsRoomWidth / 4, y + mLinsRoomWidth / 2, mPathPaint);
        canvas.drawLine(x + mLinsRoomWidth3 / 4, y - mLinsRoomWidth2, x + mLinsRoomWidth3 / 4, y + mLinsRoomWidth / 4, mPathPaint);
    }

    /**
     * 绘制降调(音符)
     */
    private void drawDownTune(Canvas canvas, float X, float startY, float stopY) {
        float x = X;
        float y = startY + mLinsRoomWidth / 2;
        Path mPath = new Path();
        //绘制一条竖线
        drawLins(canvas, x, y, x, stopY);
        mPath.moveTo(x, y + (stopY - y) / 2);
        mPath.quadTo(x + mLinsRoomWidth, stopY - (stopY - y) / 4, x, stopY);
        canvas.drawPath(mPath, mPathPaint);
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
    private void drawTial(Canvas canvas, float x, float y, boolean isDwon, String type, Notes notes) {
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
    private void drawTials(Canvas canvas, float x, float y, float x1, float y1, float x2, float y2, float x3, float y3) {
        Path mPath = new Path();
        mPath.moveTo(x, y);
        mPath.cubicTo(x1, y1, x2, y2, x3, y3);
        canvas.drawPath(mPath, mTailPaint);
    }

    /**
     * 初始化尾部连音
     *
     * @param beam
     * @param tialX
     * @param tialStartY
     * @param tialStopY
     */
    private void initLegato(List<Beam> beam, float tialX,
                            float tialStartY, float tialStopY) {
        for (int i = 0; i < beam.size(); i++) {
            Beam beam1 = beam.get(i);
            switch (beam1.getBeam()) {
                case "begin":
                    List<Legato> list = new ArrayList<>();
                    list.add(new Legato(tialX, tialStartY, tialStopY));
                    legatosMap.put(beam1.getNumber(), list);
                    break;
                case "end":
                    if (beam1.getNumber() == 1) isStopBeam = true;
                    List<Legato> lists = legatosMap.get(beam1.getNumber());
                    if (lists != null) {
                        lists.add(new Legato(tialX, tialStartY, tialStopY));
                        legatosMap.put(beam1.getNumber(), lists);
                    }
                    break;
                case "forward hook":
                    //第一个音符（向后）
                    forwardHook++;
                    break;
                case "backward hook":
                    //最后一个音符（向前）
                    backwardHook++;
                    break;
                case "continue":
                    //有这个代表连音符连接的音符数量一定大于2
                    //只有num为1的时候保存数据
                    if (beam1.getNumber() == 1 && legatosMap.get(1) != null) legatosMap.get(1)
                            .add(new Legato(tialX, tialStartY, tialStopY));
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
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth5 / 10, CenterY - mLinsRoomWidth4 / 10,
                CenterX + mLinsRoomWidth5 / 10, CenterY + mLinsRoomWidth4 / 10);
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.save();
        canvas.clipRect(new RectF(x, y, x + mLinsRoomWidth4, y + mLinsRoomWidth2));
        canvas.rotate(30, CenterX, CenterY);
        RectF rectF = new RectF(CenterX - mLinsRoomWidth3 / 10, CenterY - mLinsRoomWidth2 / 10,
                CenterX + mLinsRoomWidth3 / 10, CenterY + mLinsRoomWidth2 / 10);
        canvas.drawOval(rectF, mWhitePaint);
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
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
            if (y < twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30) {
                drawStubLins(canvas, x, (int) ((twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30) - y) / mLinsRoomWidth,
                        twoStaff_fiveLins_up + mLineNum * mLinsRoomWidth30, -1);
            } else if (twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30 < y) {
                drawStubLins(canvas, x, (int) (y - (twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30)) / mLinsRoomWidth,
                        twoStaff_fristLins_up + mLineNum * mLinsRoomWidth30, 1);
            }
        } else {
            if (y < twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30) {
                drawStubLins(canvas, x, (int) ((twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30) - y) / mLinsRoomWidth,
                        twoStaff_fiveLins_down + mLineNum * mLinsRoomWidth30, -1);
            } else if (twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30 < y) {
                drawStubLins(canvas, x, (int) (y - (twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30)) / mLinsRoomWidth,
                        twoStaff_fristLins_down + mLineNum * mLinsRoomWidth30, 1);
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
            drawLins(canvas, x, y + isReduce * num * mLinsRoomWidth, x + mLinsRoomWidth5 / 2, y + isReduce * num * mLinsRoomWidth);
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
        canvas.clipRect(new RectF(x, y, x + mLinsRoomWidth3, y + mLinsRoomWidth));
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth5 / 10, CenterY - mLinsRoomWidth3 / 10,
                CenterX + mLinsRoomWidth5 / 10, CenterY + mLinsRoomWidth3 / 10);
        canvas.rotate(-30, CenterX, CenterY);
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.restore();
        canvas.save();
        canvas.clipRect(new RectF(x, y, x + mLinsRoomWidth3, y + mLinsRoomWidth));
        RectF rectF = new RectF(CenterX - mLinsRoomWidth3 / 10, CenterY - mLinsRoomWidth2 / 10,
                CenterX + mLinsRoomWidth3 / 10, CenterY + mLinsRoomWidth2 / 10);
        canvas.rotate(-30, CenterX, CenterY);
        canvas.drawOval(rectF, mWhitePaint);
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
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
        canvas.clipRect(new RectF(x, y, x + mLinsRoomWidth3, y + mLinsRoomWidth));
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth5 / 10, CenterY - mLinsRoomWidth3 / 10,
                CenterX + mLinsRoomWidth5 / 10, CenterY + mLinsRoomWidth3 / 10);
        canvas.rotate(-30, CenterX, CenterY);//向上旋转30度
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
        }
        drawStub(canvas, x, CenterY, isFristStaff);
    }

    /**
     * 设置绘制五线谱的数据
     *
     * @param progress
     * @param iPlay
     */
    public void setStaffData(ProgresView progress, final IPlay iPlay) {
        MyLogUtils.e(TAG, "初始化五线谱");
        isUpNote = true;
        x = 0;
        fristLinsEndX.clear();
        isFrist = true;
        mFristStaffWidth = 0;
        mScendStaffWidth = 0;
        index = 0;
        start_x = 0;
        mFristStaffData = StaffDataHelper.getInstence().getmFristStaffData();
        mSecondStaffData = StaffDataHelper.getInstence().getmSecondStaffData();
        mBackUPData = StaffDataHelper.getInstence().getmBackUPData();
        mAttributess = StaffDataHelper.getInstence().getmAttributess();
        //保存整个谱子升降音的数组
        fifth = StaffDataHelper.getInstence().getFifth();
        measureDurationNum = StaffDataHelper.getInstence().getMeasureDurationNum();
        mSpeedLenth = StaffDataHelper.getInstence().getmSpeedLenth();
        isUpfifth = StaffDataHelper.getInstence().isUpfifth();
        divisions = StaffDataHelper.getInstence().getDivisions();
        beats = StaffDataHelper.getInstence().getBeats();
        isTwoStaff = StaffDataHelper.getInstence().isTowStaff();
        if (!isTwoStaff && mAttributess.getClefList().get(0).getSign().equals("F")) {
            isUpNote = false;
        }
        int num = 1;
        if (fifth != null) num = fifth.length + 1;
        measureNum = (2040 - (mBassWidth + mLinsRoomWidth * num)) / (measureDurationNum * mSpeedLenth + mLinsRoomWidth6);
        start_x = (2040 - (mBassWidth + mLinsRoomWidth * num) - (measureDurationNum * mSpeedLenth + mLinsRoomWidth6) * measureNum) / 3;
        progresView = progress;
        this.iPlay = iPlay;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startMyThered(index);
    }

    /**
     * 开启线程
     *
     * @param index
     */
    public synchronized void startMyThered(int index) {
        this.index = index;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        thread = new MysurfaceviewThread();
        thread.start();
    }

    public int getTwoStaff_fiveLins_up() {
        return twoStaff_fiveLins_up;
    }

    public int getTwoStaff_fristLins_down() {
        return twoStaff_fristLins_down;
    }

    public int getTwoStaff_fristLins_up() {
        return twoStaff_fristLins_up;
    }

    public boolean isUpNote() {
        return isUpNote;
    }

    public int getTwoStaff_fiveLins_down() {
        return twoStaff_fiveLins_down;
    }
}
