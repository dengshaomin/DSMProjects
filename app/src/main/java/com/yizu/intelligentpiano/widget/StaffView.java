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
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Legato;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.Slur;
import com.yizu.intelligentpiano.bean.Tie;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.AttributessTime;
import com.yizu.intelligentpiano.bean.xml.Beam;
import com.yizu.intelligentpiano.bean.xml.Clef;
import com.yizu.intelligentpiano.bean.xml.Measure;
import com.yizu.intelligentpiano.bean.xml.MeasureBase;
import com.yizu.intelligentpiano.bean.xml.Notes;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuxiaozhu on 2017/10/11.
 * All Rights Reserved by YiZu
 * 五线谱
 */

public class StaffView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "StaffView";

    //该view划分为6个五线谱
    private static final int STAFF_NUMS = 6;
    //一个五线谱的间数
    private static final int STAFF_LINS_NUM = 4;
    //总的间数
    private static final int STAFF_ALL_TAIL_NUMS = STAFF_NUMS * STAFF_LINS_NUM;
    //乐谱线的基本宽度
    private static final int STAFF_LINS_WSITH = 1;

    private Context mContext;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mLayoutCenterWidth;
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

    /**
     * 一个间间距
     */
    private int mLinsRoomWidth;
    private int mLinsRoomWidth2;
    private int mLinsRoomWidth3;
    private int mLinsRoomWidth4;
    private int mLinsRoomWidth5;
    private int mLinsRoomWidth6;
    private int mLinsRoomWidth7;
    private int mLinsRoomWidth8;
    private int mLinsRoomWidth9;
    private int mLinsRoomWidth10;

    //第一条五线谱总的宽度
    private int mFristStaffWidth;
    private int mScendStaffWidth;
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

    //保存绘制瀑布流的数据
    private List<PullData> pullData;

    /**
     * 钢琴移动相关
     */


    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 88;
    private IFinish iFinish;
    private List<Tie> mTie;
    private List<Slur> mSlur;
    private List<Legato> legatos;

    //保存整个谱子升降音的数组
    private String[] fifth;


    //每小节多少Duration
    private int measureDurationNum = 0;
    //每个druction的长度20像素
    private int mSpeedLenth = 20;
    //每个duration多少毫秒
    private int mSpeedTime = 0;
    //每一小节第一条五线谱的第一个音符的长度
    private List<Integer> fristSingLenth = new ArrayList<>();

    private Path mPath;

    SurfaceHolder holder;
    MysurfaceviewThread mysurfaceviewThread;
    private boolean isUpfifth = false;
    //是否保存五线谱移动的数据
    private boolean isSaveData = false;
    private boolean isMove = false;
    private int moveLenth;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        mLinsRoomWidth = mLayoutHeight / STAFF_ALL_TAIL_NUMS;
        mLinsRoomWidth2 = mLinsRoomWidth * 2;
        mLinsRoomWidth3 = mLinsRoomWidth * 3;
        mLinsRoomWidth4 = mLinsRoomWidth * 4;
        mLinsRoomWidth5 = mLinsRoomWidth * 5;
        mLinsRoomWidth6 = mLinsRoomWidth * 6;
        mLinsRoomWidth7 = mLinsRoomWidth * 7;
        mLinsRoomWidth8 = mLinsRoomWidth * 8;
        mLinsRoomWidth9 = mLinsRoomWidth * 9;
        mLinsRoomWidth10 = mLinsRoomWidth * 10;

        mLayoutCenterWidth = mLayoutHeight / 2;
        twoStaff_fristLins_up = mLayoutCenterWidth - mLinsRoomWidth4;
        twoStaff_threeLins_up = mLayoutCenterWidth - mLinsRoomWidth6;
        twoStaff_fiveLins_up = mLayoutCenterWidth - mLinsRoomWidth8;

        twoStaff_fristLins_down = mLayoutCenterWidth + mLinsRoomWidth8;
        twoStaff_threeLins_down = mLayoutCenterWidth + mLinsRoomWidth6;
        twoStaff_fiveLins_down = mLayoutCenterWidth + mLinsRoomWidth4;
        initSignScan();
        mBeamPaint.setStrokeWidth(mLinsRoomWidth / 2);
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
        isMove = false;
    }


    class MysurfaceviewThread extends Thread {

        public MysurfaceviewThread() {
            super();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
//            Looper.prepare();
            SurfaceHolder surfaceHolder = holder;
            while (true) {
                if (isMove) {
                    synchronized (surfaceHolder) {
                        //锁定canvas
                        Canvas canvas = surfaceHolder.lockCanvas();
                        try {
                            //canvas 执行一系列画的动作
                            if (canvas != null) {
                                canvas.drawColor(Color.WHITE);
                                //canvas 执行一系列画的动作
                                initStaff(canvas);
                                //释放canvas对象，并发送到SurfaceView

                            }
                        } catch (Exception e) {
                            Log.e("cdoe", e.getMessage());
                        } finally {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }

                    }
                }
            }

        }
    }

    /**
     * 绘制五线谱
     *
     * @param canvas
     */
    private void initStaff(Canvas canvas) {
        if (mAttributess != null) {
            mPath = new Path();
            mFristStaffWidth = mLinsRoomWidth2 - moveLenth;
            mScendStaffWidth = mFristStaffWidth;
            //初始化五线谱(条数)
            drawStaffLines(mAttributess, canvas);
            //绘制音符
            drawSgin(canvas);
            if (isSaveData) {
                iFinish.success();
                isSaveData = false;
            }
        }
    }


    /**
     * 绘制符号
     */
    private void drawSgin(Canvas canvas) {
        int size = mFristStaffData.size();
        for (int j = 0; j < size; j++) {
            mFristStaffWidth = Math.max(mFristStaffWidth, mScendStaffWidth);
            mScendStaffWidth = mFristStaffWidth;
//            MyLogUtils.e(TAG, "posiotion：" + j + "lenth：" + mFristStaffWidth);
            if (isSaveData) {
//                保存每一小节第一一条五线谱的以一个元素的位置
                fristSingLenth.add(mFristStaffWidth);
            }
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
            if (isTowStaff) mScendStaffWidth = mFristStaffWidth - mBackUPData.get(j) * mSpeedLenth;
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
            drawMeasureLins(canvas, j, size - 1);
            mFristStaffWidth += mSpeedLenth;
            mScendStaffWidth += mSpeedLenth;

        }
    }

    /**
     * 绘制小节线
     */
    private void drawMeasureLins(Canvas canvas, int j, int i) {
        if (j == i) {
            //绘制间线
            canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fiveLins_up, mFristStaffWidth, twoStaff_fiveLins_up, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_threeLins_up, mFristStaffWidth, twoStaff_threeLins_up, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mLinsPaint);
            canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fristLins_up, mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
            if (isTowStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);

                canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fiveLins_down, mFristStaffWidth, twoStaff_fiveLins_down, mLinsPaint);
                canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fiveLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth, mLinsPaint);
                canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_threeLins_down, mFristStaffWidth, twoStaff_threeLins_down, mLinsPaint);
                canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_threeLins_down + mLinsRoomWidth, mFristStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mLinsPaint);
                canvas.drawLine(mLinsRoomWidth2 - moveLenth, twoStaff_fristLins_down, mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
            } else {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                        mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
            }

        } else {
            canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                    mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
            if (isTowStaff) {
                canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_down,
                        mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
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
            drawRest(notes.getType(), canvas, !isFristStaff);
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
//        MyLogUtils.e(TAG, "yy" + getPatchPosiotion(notes, isFrist));
        int y = getPatchPosiotion(notes, isFristStaff);
        if (notes.getTie() != null) {
            //绘制弧形延音线
            drawTie(canvas, y, notes, isFristStaff);
        }
        if (notes.getSlur() != null) {
            drawSlur(canvas, y, notes, isFristStaff);
        }
        if (notes.getPitch().getAlter() != null) {
            //绘制升降音
            drawfitts(canvas, isFristStaff, y, notes.getPitch().getAlter());
        }
        int tialX;
        int tialY;
        if (isFristStaff) {
            switch (notes.getType()) {
                case "whole":
                    //全音符
                    drawAllHollowHeads(isAddDot, canvas, mFristStaffWidth, y);
                    break;
                case "half":
                    drawTowHollowHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        tialX = mFristStaffWidth + mLinsRoomWidth;
                        tialY = y + mLinsRoomWidth4;
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, tialX, tialY);
                    } else {
                        tialX = mFristStaffWidth + mLinsRoomWidth2;
                        tialY = y - mLinsRoomWidth3;
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, tialX, tialY);
                    }
                    break;
                case "quarter":
                    drawBlackHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth4);
                    } else {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mFristStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    }
                    break;
                case "eighth":
                    drawBlackHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth4);
                    } else {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mFristStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    }
                    if (isDrawTial) {
                        notes.getBeam();
                        drawTial(canvas, mFristStaffWidth, y, isDwon, "eighth", notes);
                    }
                    break;
                case "16th":
                    drawBlackHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth5);
                    } else {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mFristStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth4);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mFristStaffWidth, y, isDwon, "16th", notes);
                    }
                    break;
                case "32th":
                    drawBlackHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth6);
                    } else {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mFristStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth5);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mFristStaffWidth, y, isDwon, "32th", notes);
                    }
                    break;
                case "64th":
                    drawBlackHeads(isAddDot, canvas, mFristStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth7);
                    } else {
                        drawLins(canvas, mFristStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mFristStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth6);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mFristStaffWidth, y, isDwon, "64th", notes);
                    }
                    break;
            }
        } else {
            switch (notes.getType()) {
                case "whole":
                    //全音符
                    drawAllHollowHeads(isAddDot, canvas, mScendStaffWidth, y);
                    break;
                case "half":
                    drawTowHollowHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth4);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    }
                    break;
                case "quarter":
                    drawBlackHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth4);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    }
                    break;
                case "eighth":
                    drawBlackHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth4);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mScendStaffWidth, y, isDwon, "eighth", notes);
                    }
                    break;
                case "16th":
                    drawBlackHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth5);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth4);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mScendStaffWidth, y, isDwon, "16th", notes);
                    }
                    break;
                case "32th":
                    drawBlackHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth6);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth5);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mScendStaffWidth, y, isDwon, "32th", notes);
                    }
                    break;
                case "64th":
                    drawBlackHeads(isAddDot, canvas, mScendStaffWidth, y);
                    if (isDwon) {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, y + mLinsRoomWidth7);
                    } else {
                        drawLins(canvas, mScendStaffWidth + mLinsRoomWidth2, y + mLinsRoomWidth / 4, mScendStaffWidth + mLinsRoomWidth2, y - mLinsRoomWidth6);
                    }
                    if (isDrawTial) {
                        drawTial(canvas, mScendStaffWidth, y, isDwon, "64th", notes);
                    }
                    break;
            }
        }
    }

    /**
     * 红色延音线
     *
     * @param canvas
     * @param i
     * @param notes
     * @param isFristStaff
     */
    private void drawSlur(Canvas canvas, int i, Notes notes, boolean isFristStaff) {
        int x, y, z;
        if (isFristStaff) {
            x = mFristStaffWidth + mLinsRoomWidth;
        } else {
            x = mScendStaffWidth + mLinsRoomWidth;
        }
        if (notes.getStems() != null && notes.getStems().equals("up")) {
            y = i + mLinsRoomWidth / 2;
            z = y + mLinsRoomWidth;
        } else {
            y = i - mLinsRoomWidth / 2;
            z = y - mLinsRoomWidth;
        }
        int size = notes.getSlur().size();
        for (int j = 0; j < size; j++) {
            switch (notes.getSlur().get(j)) {
                case "start":
                    if (mSlur == null) {
                        mSlur = new ArrayList<>();
                    }
                    mSlur.add(new Slur(x + mLinsRoomWidth2, y));
                    break;
                case "stop":
                    Slur ties = mSlur.get(0);
                    mPath.moveTo(ties.getX(), ties.getY());
                    mPath.quadTo((ties.getX() + x) / 2, z, x - mLinsRoomWidth, y);
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
     */
    private void drawTie(Canvas canvas, int i, Notes notes, boolean isFristStaff) {
        int x, y, z;
        if (isFristStaff) {
            x = mFristStaffWidth + mLinsRoomWidth;
        } else {
            x = mScendStaffWidth + mLinsRoomWidth;
        }
        if (notes.getStems() != null && notes.getStems().equals("up")) {
            y = i + mLinsRoomWidth / 2;
            z = y + mLinsRoomWidth;
        } else {
            y = i - mLinsRoomWidth / 2;
            z = y - mLinsRoomWidth;
        }
        int size = notes.getTie().size();
        for (int j = 0; j < size; j++) {
            switch (notes.getTie().get(j)) {
                case "start":
                    if (mTie == null) {
                        mTie = new ArrayList<>();
                    }
                    mTie.add(new Tie(x + mLinsRoomWidth2, y));
                    break;
                case "stop":
                    Tie ties = mTie.get(0);
                    mPath.moveTo(ties.getX(), ties.getY());
                    mPath.quadTo((ties.getX() + x) / 2, z, x - mLinsRoomWidth, y);
//                    canvas.drawPath(mPath, mCrudePaint);
                    canvas.drawPath(mPath, mLinsPaint);
                    mTie.remove(0);
                    break;
            }
        }
    }

    /**
     * 绘制每个音符是否升降音
     */
    private void drawfitts(Canvas canvas, boolean isFristStaff, int y, String alter) {
        int alters = Integer.valueOf(alter);
        int x;
        if (isFristStaff) {
            x = mFristStaffWidth;
        } else {
            x = mScendStaffWidth;
        }
        switch (alters) {
            case -2:
                //重降 bb
                drawDownTune(canvas, x - mLinsRoomWidth, y, x - mLinsRoomWidth, y + mLinsRoomWidth2);
                x += mLinsRoomWidth;
                drawDownTune(canvas, x, y, x, y + mLinsRoomWidth2);
                break;
            case -1:
                //降 b
                drawDownTune(canvas, x, y, x, y + mLinsRoomWidth2);
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
            mFristStaffWidth += mLinsRoomWidth;
        } else {
            mScendStaffWidth += mLinsRoomWidth;
        }
    }

    /**
     * 绘制休止符
     */
    private void drawRise(Canvas canvas, int x, int y, int alter) {
        if (alter == 1) {
//            #
            canvas.drawLine(x - mLinsRoomWidth, y + mLinsRoomWidth / 4, x + mLinsRoomWidth, y + mLinsRoomWidth / 4, mTailPaint);
            canvas.drawLine(x - mLinsRoomWidth, y + mLinsRoomWidth5 / 4, x + mLinsRoomWidth, y + mLinsRoomWidth5 / 4, mTailPaint);
            canvas.drawLine(x - mLinsRoomWidth / 2, y - mLinsRoomWidth / 2, x - mLinsRoomWidth / 2, y + mLinsRoomWidth2, mTailPaint);
            canvas.drawLine(x + mLinsRoomWidth / 2, y - mLinsRoomWidth / 2, x + mLinsRoomWidth / 2, y + mLinsRoomWidth2, mTailPaint);
        } else {
            //x
            drawLins(canvas, x, y, x + mLinsRoomWidth, y + mLinsRoomWidth);
            drawLins(canvas, x + mLinsRoomWidth, y + mLinsRoomWidth, x, y);
        }
    }

    /**
     * 绘制还原符号
     */
    private void drawReduction(Canvas canvas, int x, int y) {
        drawLins(canvas, x, y - mLinsRoomWidth, x, y + mLinsRoomWidth);
        drawLins(canvas, x + mLinsRoomWidth, y - mLinsRoomWidth / 2, x + mLinsRoomWidth, y + mLinsRoomWidth3 / 2);
        drawLins(canvas, x, y, x, y - mLinsRoomWidth / 2);
        drawLins(canvas, x, y + mLinsRoomWidth, x, y + mLinsRoomWidth / 2);
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
            return posiotion + mLinsRoomWidth * (7 / 2) * (3 - pa);
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
                    posiotion = twoStaff_fristLins_up - mLinsRoomWidth * 3 / 2;
                    break;
                case "A":
                    posiotion = twoStaff_threeLins_up;
                    break;
                case "B":
                    posiotion = twoStaff_threeLins_up - mLinsRoomWidth / 2;
                    break;
            }
            return posiotion + mLinsRoomWidth * (7 / 2) * (4 - pa);
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
        //休止符
        switch (rest) {
            case "long":
                //4音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth, mCrudePaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth, mCrudePaint);
                }
                break;
            case "breve":
                //2音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down, mCrudePaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up, mCrudePaint);
                }
                break;
            case "whole":
                //1音节
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth, mCrudePaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth, mCrudePaint);
                }
                break;
            case "half":
                //二分休止符
                if (inTowStaffs) {
                    //第二条线
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - STAFF_LINS_WSITH * 4, mCrudePaint);
                } else {
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - STAFF_LINS_WSITH * 4, mCrudePaint);
                }
                break;
            case "quarter":
                //四分休止符
                if (inTowStaffs) {
                    //第二条线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down - mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down, mCrudePaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth + mLinsRoomWidth, twoStaff_fristLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down - mLinsRoomWidth, mScendStaffWidth, twoStaff_fristLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up - mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up, mCrudePaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth, mFristStaffWidth, twoStaff_fristLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                }
                break;
            case "eighth":
                //八分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                }
                break;
            case "16th":
                //16分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 4 / 3, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                }
                break;
            case "32th":
                //32分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_down - mLinsRoomWidth, mLinsPaint);
                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 / 2, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2, mFristStaffWidth + mLinsRoomWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 2, twoStaff_threeLins_up - mLinsRoomWidth, mLinsPaint);
                }
                break;
            case "64th":
                //64分休止符
                if (inTowStaffs) {
                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 5);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down, mLinsPaint);

                } else {
                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 5, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 5, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2, mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 5);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up, mLinsPaint);

                }
                break;
            case "128th":
                //128分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 2 / 6);
                    canvas.drawPath(mPath, mLinsPaint);

//                        mPath.moveTo(mFristStaffWidth, twoStaff_fristLins_down);
//                        mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 / 6, twoStaff_fristLins_down);
//                        canvas.drawPath(mPath, mLinsPaint);

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth, mLinsPaint);

                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 6, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 6, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 3 / 6, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2, mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 2 / 6);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth, mLinsPaint);
                }
                break;
            case "256th":
                //256分休止符
                if (inTowStaffs) {
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    //第五线
                    mPath.moveTo(mScendStaffWidth, twoStaff_fiveLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fiveLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 7 / 7, twoStaff_fiveLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //第四线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down - mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down, mScendStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_down - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地三线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_down);
                    canvas.drawPath(mPath, mLinsPaint);
                    //地二线
                    mPath.moveTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth);
                    mPath.quadTo(mScendStaffWidth, twoStaff_threeLins_down + mLinsRoomWidth * 2, mScendStaffWidth + mLinsRoomWidth, twoStaff_threeLins_down + mLinsRoomWidth * 3 * 3 / 7);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mScendStaffWidth, twoStaff_fristLins_down);
                    mPath.quadTo(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth, mScendStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_down);
                    canvas.drawPath(mPath, mLinsPaint);

                    canvas.drawLine(mScendStaffWidth, twoStaff_fristLins_down + mLinsRoomWidth * 2, mScendStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_down - mLinsRoomWidth, mLinsPaint);

                } else {

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mFristStaffWidth, twoStaff_fiveLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fiveLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 6 / 7, twoStaff_fiveLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up - mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up, mFristStaffWidth + mLinsRoomWidth * 3 * 5 / 7, twoStaff_threeLins_up - mLinsRoomWidth);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 4 / 7, twoStaff_threeLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    mPath.moveTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth);
                    mPath.quadTo(mFristStaffWidth, twoStaff_threeLins_up + mLinsRoomWidth * 2, mFristStaffWidth + mLinsRoomWidth, twoStaff_fristLins_up - mLinsRoomWidth * 3 * 3 / 7);
                    canvas.drawPath(mPath, mLinsPaint);

                    mPath.moveTo(mFristStaffWidth, twoStaff_fristLins_up);
                    mPath.quadTo(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth, mFristStaffWidth + mLinsRoomWidth * 3 * 2 / 7, twoStaff_fristLins_up);
                    canvas.drawPath(mPath, mLinsPaint);
                    canvas.drawLine(mFristStaffWidth, twoStaff_fristLins_up + mLinsRoomWidth * 2, mFristStaffWidth + mLinsRoomWidth * 3, twoStaff_fiveLins_up - mLinsRoomWidth, mLinsPaint);
                }
                break;
        }
    }

    /**
     * 初始化五线谱
     *
     * @param attributess
     * @param canvas
     */
    private void drawStaffLines(Attributess attributess, Canvas canvas) {
        if (isTowStaff) {
            canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                    mFristStaffWidth, twoStaff_fristLins_down, mLinsPaint);
        } else {
            canvas.drawLine(mFristStaffWidth, twoStaff_fiveLins_up,
                    mFristStaffWidth, twoStaff_fristLins_up, mLinsPaint);
        }
        //绘制音符
        drawSign(canvas, attributess.getClefList());
        int wigth = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth = wigth;
        mScendStaffWidth = wigth;
        //绘制节拍
        drawTimes(canvas, attributess.getTime());

        mFristStaffWidth = Math.max(mFristStaffWidth, mScendStaffWidth);
        mFristStaffWidth += mSpeedLenth;
        mScendStaffWidth = mFristStaffWidth;
    }

    /**
     * 拍子
     *
     * @param canvas
     * @param time
     */
    private void drawTimes(Canvas canvas, AttributessTime time) {
        drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_up - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_threeLins_up);
        drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_up - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_up);
        if (isTowStaff) {
            drawTime(canvas, time.getBeats(), mFristStaffWidth, twoStaff_threeLins_down - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_threeLins_down);
            drawTime(canvas, time.getBeat_type(), mFristStaffWidth, twoStaff_fristLins_down - mNumHeight, mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_down);
        }
        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth;
    }

    /**
     * @param canvas
     * @param beat
     */
    private void drawTime(Canvas canvas, String beat, int left, int top, int rifht, int bottom) {
        switch (beat) {
            case "2":
                twoDrawable.setBounds(left, top, rifht, bottom);
                twoDrawable.draw(canvas);
                break;
            case "3":
                threeDrawable.setBounds(left, top, rifht, bottom);
                threeDrawable.draw(canvas);
                break;
            case "4":
                fourDrawable.setBounds(left, top, rifht, bottom);
                fourDrawable.draw(canvas);
                break;
            case "6":
                sixDrawable.setBounds(left, top, rifht, bottom);
                sixDrawable.draw(canvas);
                break;
            case "8":
                eightDrawable.setBounds(left, top, rifht, bottom);
                eightDrawable.draw(canvas);
                break;
            case "9":
                nineDrawable.setBounds(left, top, rifht, bottom);
                nineDrawable.draw(canvas);
                break;
            case "12":
                twelveDrawable.setBounds(left, top, rifht, bottom);
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
//        MyLogUtils.e(TAG, "mFristStaffWidth:" + mFristStaffWidth);
        //两条五线谱
        for (int j = 0; j < clefList.size(); j++) {
            if (j == 0) {
                switch (clefList.get(j).getSign()) {
                    case "G":
                        //高音
                        drawTreble(canvas, mFristStaffWidth, mLayoutCenterWidth - mLinsRoomWidth2 - mTrebleHeight,
                                mFristStaffWidth + mTrebleWidth, mLayoutCenterWidth - mLinsRoomWidth2);
                        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth2;
                        drawFifths(canvas, true, true);
                        break;
                    case "F":
                        drawBass(canvas, mFristStaffWidth, twoStaff_fristLins_up - mLinsRoomWidth - mBassHeight,
                                mFristStaffWidth + mTrebleWidth, twoStaff_fristLins_up - mLinsRoomWidth);
                        mFristStaffWidth += mTrebleWidth + mLinsRoomWidth2;
                        drawFifths(canvas, true, false);
                        break;
                }
            } else {
                switch (clefList.get(j).getSign()) {
                    case "G":
                        //高音
                        drawTreble(canvas, mScendStaffWidth, mLayoutCenterWidth + mLinsRoomWidth3,
                                mScendStaffWidth + mTrebleWidth, mLayoutCenterWidth + mLinsRoomWidth3 + mTrebleHeight);
                        mScendStaffWidth += mTrebleWidth + mLinsRoomWidth2;
                        drawFifths(canvas, false, true);
                        break;
                    case "F":
                        //低音
                        drawBass(canvas, mScendStaffWidth, mLayoutCenterWidth + mLinsRoomWidth4,
                                mScendStaffWidth + mBassWidth, mLayoutCenterWidth + mLinsRoomWidth4 + mBassHeight);
                        mScendStaffWidth += mTrebleWidth + mLinsRoomWidth2;
                        drawFifths(canvas, false, false);
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
    private void drawBass(Canvas canvas, int left, int top, int rifht, int bottom) {
        bassDrawable.setBounds(left, top, rifht, bottom);
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
    private void drawTreble(Canvas canvas, int left, int top, int rifht, int bottom) {
        trebleDrawable.setBounds(left, top, rifht, bottom);
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
//        MyLogUtils.e(TAG, "mFristStaffWidth:" + mFristStaffWidth);
        if (fifth == null || fifth.length == 0) {
            return;
        }
        // TODO: 2017/10/31 升降音未处理处理
        if (isUpfifth) {
            //升调
        } else {
            //降调
        }
//        if (fifths < 0) {
//            //降调
//            fifths = Math.abs(fifths);
//            while (fifths != 0) {
//                drawDownTune(canvas, mFristStaffWidth, mLayoutCenterWidth - mLinsRoomWidth * (7 + fifths % 2), mFristStaffWidth, mLayoutCenterWidth - mLinsRoomWidth * (4 + fifths % 2));
//                if (isTowStaff) {
//                    drawDownTune(canvas, mFristStaffWidth, mLayoutCenterWidth + mLinsRoomWidth * (5 - fifths % 2), mFristStaffWidth, mLayoutCenterWidth + mLinsRoomWidth * (8 - fifths % 2));
//                }
//                mFristStaffWidth += mLinsRoomWidth3;
//                fifths--;
//            }
//        } else {
//            //升调
//            while (fifths != 0) {
//                drawUpTune(canvas, mFristStaffWidth, mLayoutCenterWidth - mLinsRoomWidth * (7 + fifths % 2), mFristStaffWidth, mLayoutCenterWidth - mLinsRoomWidth * (4 + fifths % 2));
//                if (isTowStaff) {
//                    drawUpTune(canvas, mFristStaffWidth, mLayoutCenterWidth + mLinsRoomWidth * (5 - fifths % 2), mFristStaffWidth, mLayoutCenterWidth + mLinsRoomWidth * (8 - fifths % 2));
//                }
//                mFristStaffWidth += mLinsRoomWidth3;
//                fifths--;
//            }
//        }
        if (isfrist) {
            mFristStaffWidth += mLinsRoomWidth2;
        } else {
            mScendStaffWidth += mLinsRoomWidth2;
        }

    }

    /**
     * 绘制升调
     */
    private void drawUpTune(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        drawLins(canvas, startX, startY, stopX, stopY);
        drawLins(canvas, startX + mLinsRoomWidth, startY - mLinsRoomWidth / 2, stopX + mLinsRoomWidth, stopY - mLinsRoomWidth / 2);
        canvas.drawLine(startX - mLinsRoomWidth / 2, startY + (stopY - startY) / 3, startX + mLinsRoomWidth * 3 / 2, startY - mLinsRoomWidth + (stopY - startY) / 3, mTailPaint);
        canvas.drawLine(startX - mLinsRoomWidth / 2, startY + (stopY - startY) * 2 / 3, startX + mLinsRoomWidth * 3 / 2, startY - mLinsRoomWidth + (stopY - startY) * 2 / 3, mTailPaint);
    }

    /**
     * 绘制降调
     */
    private void drawDownTune(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        //绘制一条竖线
        drawLins(canvas, startX, startY, stopX, stopY);
        mPath.moveTo(startX, startY + (stopY - startY) / 2);
        //二介
        mPath.quadTo(startX + mLinsRoomWidth * 2, startY, stopX, stopY);
        //三介
//        mPath.cubicTo();
        canvas.drawPath(mPath, mTailPaint);
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
        if (notes.getBeam() != null) {
            drawLegato(notes.getBeam(), type, isDwon, (int) x, (int) y, canvas);
            return;
        }
        switch (type) {
            case "eighth":
                if (isDwon) {
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                } else {
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    canvas.drawPath(mPath, mTailPaint);
                }
                break;
            case "16th":
                if (isDwon) {
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth5);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                    canvas.drawPath(mPath, mTailPaint);
                } else {
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                }
                break;
            case "32th":
                if (isDwon) {
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth5);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth6);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth5, x + mLinsRoomWidth2, y + mLinsRoomWidth5, x + mLinsRoomWidth2, y + mLinsRoomWidth4);
                    canvas.drawPath(mPath, mTailPaint);
                } else {
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth5);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth4, x + mLinsRoomWidth3, y - mLinsRoomWidth4, x + mLinsRoomWidth3, y - mLinsRoomWidth3);
                    canvas.drawPath(mPath, mTailPaint);
                }
                break;
            case "64th":
                if (isDwon) {
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth3, x + mLinsRoomWidth2, y + mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth5);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth4, x + mLinsRoomWidth2, y + mLinsRoomWidth3);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth6);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth5, x + mLinsRoomWidth2, y + mLinsRoomWidth5, x + mLinsRoomWidth2, y + mLinsRoomWidth4);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth, y + mLinsRoomWidth7);
                    mPath.cubicTo(x + mLinsRoomWidth, y + mLinsRoomWidth6, x + mLinsRoomWidth2, y + mLinsRoomWidth6, x + mLinsRoomWidth2, y + mLinsRoomWidth5);
                    canvas.drawPath(mPath, mTailPaint);
                } else {
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth2, x + mLinsRoomWidth3, y - mLinsRoomWidth);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth4);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth3, x + mLinsRoomWidth3, y - mLinsRoomWidth2);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth5);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth4, x + mLinsRoomWidth3, y - mLinsRoomWidth4, x + mLinsRoomWidth3, y - mLinsRoomWidth3);
                    canvas.drawPath(mPath, mTailPaint);
                    mPath.moveTo(x + mLinsRoomWidth2, y - mLinsRoomWidth6);
                    mPath.cubicTo(x + mLinsRoomWidth2, y - mLinsRoomWidth5, x + mLinsRoomWidth3, y - mLinsRoomWidth5, x + mLinsRoomWidth3, y - mLinsRoomWidth4);
                    canvas.drawPath(mPath, mTailPaint);
                }
                break;
        }
    }

    /**
     * 绘制尾部连音
     *
     * @param beam
     * @param type
     * @param isDwon
     * @param x
     * @param y
     * @param canvas
     */
    private void drawLegato(List<Beam> beam, String type, boolean isDwon, int x, int y, Canvas canvas) {
        if (legatos == null) {
            legatos = new ArrayList<>();
        }
        for (int i = 0; i < beam.size(); i++) {
            int X = 0;
            int Y = 0;
            switch (type) {
                case "eighth":
                    if (isDwon) {
                        X = x + mLinsRoomWidth;
                        Y = y + mLinsRoomWidth4;
                    } else {
                        X = x + mLinsRoomWidth2;
                        Y = y - mLinsRoomWidth3;
                    }
                    break;
                case "16th":
                    if (isDwon) {
                        X = x + mLinsRoomWidth;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y + mLinsRoomWidth5;
                                break;
                            case "2":
                                Y = y + mLinsRoomWidth4;
                                break;
                        }
                    } else {
                        X = x + mLinsRoomWidth2;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y - mLinsRoomWidth4;
                                break;
                            case "2":
                                Y = y - mLinsRoomWidth3;
                                break;
                        }
                    }
                    break;
                case "32th":
                    if (isDwon) {
                        X = x + mLinsRoomWidth;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y + mLinsRoomWidth6;
                                break;
                            case "2":
                                Y = y + mLinsRoomWidth5;
                                break;
                            case "3":
                                Y = y + mLinsRoomWidth4;
                                break;
                        }
                    } else {
                        X = x + mLinsRoomWidth2;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y - mLinsRoomWidth5;
                                break;
                            case "2":
                                Y = y - mLinsRoomWidth4;
                                break;
                            case "3":
                                Y = y - mLinsRoomWidth3;
                                break;
                        }
                    }
                    break;
                case "64th":
                    if (isDwon) {
                        X = x + mLinsRoomWidth;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y + mLinsRoomWidth7;
                                break;
                            case "2":
                                Y = y + mLinsRoomWidth6;
                                break;
                            case "3":
                                Y = y + mLinsRoomWidth5;
                                break;
                            case "4":
                                Y = y + mLinsRoomWidth4;
                                break;
                        }
                    } else {
                        X = x + mLinsRoomWidth2;
                        switch (beam.get(i).getNumber()) {
                            case "1":
                                Y = y - mLinsRoomWidth6;
                                break;
                            case "2":
                                Y = y - mLinsRoomWidth5;
                                break;
                            case "3":
                                Y = y - mLinsRoomWidth4;
                                break;
                            case "4":
                                Y = y - mLinsRoomWidth3;
                                break;
                        }
                    }
            }
            switch (beam.get(i).getBeam()) {
                case "begin":
                    legatos.add(new Legato(beam.get(i).getNumber(), X, Y));
                    break;
                case "end":
                    for (int j = 0; j < legatos.size(); j++) {
                        if (beam.get(i).getNumber().equals(legatos.get(j).getNumber())) {
                            canvas.drawLine(legatos.get(j).getX(), legatos.get(j).getY(), X, Y, mBeamPaint);
                            legatos.remove(j);
                        }
                    }
                    break;
                case "forward hook":
                    canvas.drawLine(X, Y, X + mLinsRoomWidth, Y, mBeamPaint);
                    break;
                case "backward hook":
                    canvas.drawLine(X - mLinsRoomWidth, Y, X, Y, mBeamPaint);
                    break;
            }


        }
    }

    /**
     * 全音符符头
     *
     * @param isAddDot 是否添加圆点
     * @param canvas
     */
    private void drawAllHollowHeads(boolean isAddDot, Canvas canvas, float x, float y) {
        float CenterX = x + mLinsRoomWidth3 / 2;
        float CenterY = y + mLinsRoomWidth / 2;
        canvas.save();
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 2, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 2);
        canvas.drawOval(rectFs, mBlackPaint);
        RectF rectF = new RectF(CenterX - mLinsRoomWidth / 2, CenterY - mLinsRoomWidth / 4, CenterX + mLinsRoomWidth / 2, CenterY + mLinsRoomWidth / 4);
        canvas.rotate(30, CenterX, CenterY);
        canvas.drawOval(rectF, mWhitePaint);
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
        }
    }

    /**
     * 二分音符的符头
     *
     * @param isAddDot
     * @param canvas
     */
    private void drawTowHollowHeads(boolean isAddDot, Canvas canvas, float x, float y) {
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
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
        }
    }

    /**
     * 绘制纯黑色符头
     * 该符头主要是四分音符以后的符头
     *
     * @param isAddDot
     * @param canvas
     * @param x        绘制符头的中心左上x坐标
     * @param y        绘制符头的左上y坐标
     */
    private void drawBlackHeads(boolean isAddDot, Canvas canvas, float x, float y) {
        float CenterX = x + mLinsRoomWidth3 / 2;
        float CenterY = y + mLinsRoomWidth / 2;
        canvas.save();
        RectF rectFs = new RectF(CenterX - mLinsRoomWidth3 / 4, CenterY - mLinsRoomWidth / 3, CenterX + mLinsRoomWidth3 / 4, CenterY + mLinsRoomWidth / 3);
        canvas.rotate(-30, CenterX, CenterY);//向上旋转30度
        canvas.drawOval(rectFs, mBlackPaint);
        canvas.restore();
        if (isAddDot) {
            canvas.drawCircle(CenterX + mLinsRoomWidth3 / 2, CenterY, mLinsRoomWidth / 3, mBlackPaint);
        }
    }

    /**
     * 设置绘制五线谱的数据
     *
     * @param staffData
     */
    public void setStaffData(List<Measure> staffData, IFinish iFinish) {
        initStaffData();
        MyLogUtils.e(TAG, "setStaffData");
        if (staffData == null) {
            MyToast.ShowLong("五线谱数据为空");
            return;
        }
        if (iFinish != null) {
            this.iFinish = iFinish;
        }
        if (mFristStaffData != null) {
            mFristStaffData.clear();
        } else {
            mFristStaffData = new ArrayList<>();
        }
        if (mSecondStaffData != null) {
            mSecondStaffData.clear();
        } else {
            mSecondStaffData = new ArrayList<>();
        }
        isSaveData = true;
        //初始化瀑布流的数据
        if (pullData == null) {
            pullData = new ArrayList<>();
        } else {
            pullData.clear();
        }
        //将mAttributess置空
        mAttributess = null;
        fifth = null;
        //该音符之前的总duration
        int fristTimeDuration = 0;
        int secondTimeDuration = 0;
        //总node个数，用于计分
        int totalNodes = 0;
        for (int j = 0; j < staffData.size(); j++) {
            totalNodes += staffData.get(j).getMeasure().size();
            List<MeasureBase> list = new ArrayList<>();
            List<MeasureBase> list1 = new ArrayList<>();
            List<SaveTimeData> fristTime = new ArrayList<>();
            List<SaveTimeData> secondTime = new ArrayList<>();

            boolean isBackUp = false;
            int measureSize = staffData.get(j).getMeasure().size();
            for (int k = 0; k < measureSize; k++) {
                if (mAttributess == null && staffData.get(j).getMeasure().get(k).getAttributes() != null) {
                    //五线谱信息
                    mAttributess = staffData.get(j).getMeasure().get(k).getAttributes();
                    //处理整条五线谱的升降音
                    initFifthData(mAttributess.getKey().getFifths());
                    if (mAttributess.getStaves().equals("2")) {
                        isTowStaff = true;
                        if (mBackUPData == null) {
                            mBackUPData = new ArrayList<>();
                        } else {
                            mBackUPData.clear();
                        }
                    } else {
                        isTowStaff = false;
                    }
                } else if (staffData.get(j).getMeasure().get(k).getSound() != null && !staffData.get(j).getMeasure().get(k).getSound().equals("")) {
//                    建议拍数
                    DEFAULT_TIME_NUM = Integer.valueOf(staffData.get(j).getMeasure().get(k).getSound());
                } else {
                    if (staffData.get(j).getMeasure().get(k).getBackup() != null) {
                        isBackUp = true;
//                        往前移动backup个距离
                        secondTimeDuration = fristTimeDuration - Integer.valueOf(staffData.get(j).getMeasure().get(k).getBackup().getDuration());
                        //保存backup数据
                        mBackUPData.add(Integer.valueOf(staffData.get(j).getMeasure().get(k).getBackup().getDuration()));
                    } else {
                        if (!isBackUp) {
                            list.add(staffData.get(j).getMeasure().get(k));
                            Notes notes = staffData.get(j).getMeasure().get(k).getNotes();
                            if (notes != null) {
                                if (notes.getRest()) {
                                    fristTime.add(new SaveTimeData(fristTimeDuration, Integer.valueOf(notes.getDuration()), true));
                                    fristTimeDuration += Integer.valueOf(notes.getDuration());
                                } else if (notes.getPitch() != null) {

                                    //重新设置瀑布流数据
                                    fristTimeDuration = setPullView(fristTime, fristTimeDuration, notes);
                                }
                            }
                        } else {
                            //五线谱第二条线
                            list1.add(staffData.get(j).getMeasure().get(k));
                            //瀑布流第二线数据
                            Notes notes = staffData.get(j).getMeasure().get(k).getNotes();
                            if (notes != null) {
                                if (notes.getRest()) {
                                    secondTime.add(new SaveTimeData(secondTimeDuration, Integer.valueOf(notes.getDuration()), true));
                                    secondTimeDuration += Integer.valueOf(notes.getDuration());
                                } else if (notes.getPitch() != null) {
                                    secondTimeDuration = setPullView(secondTime, secondTimeDuration, notes);
//                                    MyLogUtils.e(TAG, "ccc" + secondTimeDuration);
                                }
                            }

                        }
                    }
                }
            }
            mFristStaffData.add(new Measure(list));
            mSecondStaffData.add(new Measure(list1));
            pullData.add(new PullData(fristTime, secondTime));
        }
        //每个duraction的时间
        mSpeedTime = 60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions()));
        //每一小节的duraction数量
        measureDurationNum = Integer.valueOf(mAttributess.getDivisions()) * Integer.valueOf(mAttributess.getTime().getBeats());
        if (mysurfaceviewThread == null) {
            mysurfaceviewThread = new MysurfaceviewThread();
            isMove = true;
            mysurfaceviewThread.start();
        }
    }

    /**
     * 初始化五线谱数据
     */
    private void initStaffData() {
        mFristStaffWidth = 0;
        mScendStaffWidth = 0;
        //true：二条五线谱上
        isTowStaff = false;
        //是否绘制符尾（八分音符以后的倾斜符尾）
        isDrawTial = false;
        if (mFristStaffData != null) mFristStaffData.clear();
        if (mSecondStaffData != null) mSecondStaffData.clear();
        if (mBackUPData != null) mBackUPData.clear();
        if (mTie != null) mTie.clear();
        if (mSlur != null) mSlur.clear();
        if (legatos != null) legatos.clear();
        if (fristSingLenth != null) fristSingLenth.clear();
        mAttributess = null;
//是否保存五线谱移动的数据
        isSaveData = true;
        DEFAULT_TIME_NUM = 88;
        iFinish = null;
        //保存整个谱子升降音的数组
        fifth = null;
        measureDurationNum = 0;
        mSpeedTime = 0;
        isMove = false;
        moveLenth = 0;
//        if (mysurfaceviewThread != null) {
//            mysurfaceviewThread.interrupt();
//            mysurfaceviewThread = null;
//        }
    }

    /**
     * 从新设置瀑布流数据
     *
     * @param list
     * @param duration
     * @param notes
     */
    private int setPullView(List<SaveTimeData> list, int duration, Notes notes) {
        //键组
        int octave = Integer.valueOf(notes.getPitch().getOctave());
        //音域
        String step = notes.getPitch().getStep();
        String saveStep = step;
        String alter = notes.getPitch().getAlter();//本音符是否生姜
        int black = 0;

        //处理单个音符的升降
        if (alter != null) {
            if (octave == 0) {
                switch (step) {
                    case "A":
                        switch (alter) {
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "B";
                                break;
                        }
                        break;
                    case "B":
                        switch (alter) {
                            case "-2":
                                step = "A";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "C";
                                octave++;
                                black = 0;
                                break;
                        }
                        break;
                }
            } else {
                switch (step) {
                    case "C":
                        switch (alter) {
                            case "-2":
                                step = "B";
                                octave--;
                                black = 0;
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "D";
                                break;
                        }
                        break;
                    case "D":
                        switch (alter) {
                            case "-2":
                                step = "C";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "E";
                                break;
                        }
                        break;
                    case "E":
                        switch (alter) {
                            case "-2":
                                step = "D";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "F";
                                black = 0;
                                break;
                        }
                        break;
                    case "F":
                        switch (alter) {
                            case "-2":
                                step = "E";
                                black = -0;
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "G";
                                break;
                        }
                        break;
                    case "G":
                        switch (alter) {
                            case "-2":
                                step = "F";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "A";
                                break;
                        }
                        break;
                    case "A":
                        switch (alter) {
                            case "-2":
                                step = "G";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "B";
                                break;
                        }
                        break;
                    case "B":
                        switch (alter) {
                            case "-2":
                                step = "A";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "C";
                                octave++;
                                black = 0;
                                break;
                        }
                        break;
                }
            }
        }

        //整条五线谱升降调
        if (fifth != null) {
            for (int i = 0; i < fifth.length; i++) {
                if (fifth[i] == saveStep && !alter.equals("0")) {
                    if (octave == 0) {
                        switch (step) {
                            case "A":
                                if (isUpfifth) {
                                    if (black == 1) {
                                        step = "B";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                }
                                break;
                            case "B":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        octave = 1;
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                        }
                    } else {
                        switch (step) {
                            case "C":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "D";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        octave = 0;
                                        step = "B";
                                    } else {
                                        black--;
                                    }

                                }
                                break;
                            case "D":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "E";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "E":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "F";
                                        black = 0;
                                    } else {
                                        black++;
                                    }

                                } else {
                                    if (black == -1) {
                                        step = "D";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "F":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "G";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "E";
                                        black = 0;
                                    } else {
                                        black--;
                                    }

                                }
                                break;
                            case "G":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "F";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "A":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "B";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "G";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "B":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        octave++;
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
        SaveTimeData time = new SaveTimeData(duration, Integer.valueOf(notes.getDuration()), octave, step, black);

        if (!notes.getChord()) {
            duration += Integer.valueOf(notes.getDuration());
        } else {
            time.setmAddDuration(list.get(list.size() - 1).getmAddDuration());
        }
        list.add(time);
        return duration;
    }

    /**
     * 初始化整个五线谱的升降音
     *
     * @param fifths
     */
    private void initFifthData(String fifths) {
        switch (fifths) {
            case "1":
                isUpfifth = true;
                fifth = new String[]{"F"};
                break;
            case "2":
                isUpfifth = true;
                fifth = new String[]{"F", "C"};
                break;
            case "3":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G"};
                break;
            case "4":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D"};
                break;
            case "5":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A"};
                break;
            case "6":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A", "E"};
                break;
            case "7":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A", "E", "B"};
                break;
            case "-1":
                fifth = new String[]{"B"};
                break;
            case "-2":
                fifth = new String[]{"B", "E"};
                break;
            case "-3":
                fifth = new String[]{"B", "E", "A"};
                break;
            case "-4":
                fifth = new String[]{"B", "E", "A", "D"};
                break;
            case "-5":
                fifth = new String[]{"B", "E", "A", "D", "G"};
                break;
            case "-6":
                fifth = new String[]{"B", "E", "A", "D", "G", "C"};
                break;
            case "-7":
                fifth = new String[]{"B", "E", "A", "D", "G", "C", "F"};
                break;
        }
    }


    public Attributess getmAttributess() {
        return mAttributess;
    }

    public List<PullData> getPullData() {
        return pullData;
    }

    public int getmSpeedTime() {
        return mSpeedTime;
    }

    public int getmLinsRoomWidth() {
        return mLinsRoomWidth;
    }

    public int getTwoStaff_fristLins_up() {
        return twoStaff_fristLins_up;
    }

    public boolean isTowStaff() {
        return isTowStaff;
    }

    public List<Integer> getFristSingLenth() {
        return fristSingLenth;
    }

    public int getmSpeedLenth() {
        return mSpeedLenth;
    }

    /**
     * 拍子
     *
     * @return
     */
    public int getTimes() {
        return DEFAULT_TIME_NUM;
    }

    /**
     * 五线谱移动
     *
     * @param lenth
     */
    public void remove(int lenth) {
        moveLenth = lenth;
        isMove = true;
        if (mysurfaceviewThread == null) {
            mysurfaceviewThread = new MysurfaceviewThread();
            mysurfaceviewThread.start();
        }
        MyLogUtils.e(TAG, "lenth：" + moveLenth);
    }

    public void resetPullView() {
        isMove = false;
        isUpfifth = false;

        isSaveData = false;
        moveLenth = 0;
        if (mysurfaceviewThread != null) {
            mysurfaceviewThread.interrupt();
            mysurfaceviewThread = null;
        }
    }
}
