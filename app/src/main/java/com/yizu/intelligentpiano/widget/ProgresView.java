package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/12/13.
 * Email: chenhuixueba@163.com
 * 进度条
 */

public class ProgresView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "ProgresView";
    private SurfaceHolder holder;
    private MyThered myThered;
    private boolean isMove = false;
    //进度条矩形
    private RectF mProgessRectF;
    //绘制进度条的画笔
    private Paint mBluePaint;
    private int mLayoutHeight;
    private float mReta = 0.8f;//80拍
    private float mSpeed = 0;
    //进度条顶部
    private int top = 0;
    //进度条底部
    private int bottom = 0;
    //加一行的长度
    private int mLinsRoomWidth30 = 0;
    private int mLinsRoomWidth4 = 0;
    //第一行开始的位置
    private float startX = 0;
    //第一行结束的位置
//    private float stopX = 0;
    //每一行的小结数
    private int measureNum = 0;
    //最后结束的行数
    private int endNum = 0;
    //用来记录走过的行数
    private int num = 0;
    //最后一行的节数
    private int endMeasureNum = 0;

    private int index = 0;

    private float progressX = 0;
    private StaffView mStaffView;
    private boolean isPush = false;
    private IPlayEnd iPlayEnd;
    //第一行每小节结尾的位置
    private List<Float> fristLinsEndX = new ArrayList();
    private int posiotion = 0;


    public ProgresView(Context context) {
        this(context, null);
    }

    public ProgresView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgresView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
        mBluePaint = new Paint();
        mBluePaint.setAntiAlias(true);
        mBluePaint.setColor(getResources().getColor(R.color.violet));
        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setAlpha(100);//0-255
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            isMove = false;
        }
    }

    public void onDrestry() {
        isMove = false;
        if (myThered != null) {
            myThered.interrupt();
            myThered = null;
        }
    }

    public void setPlayEnd(IPlayEnd iPlayEnd) {
        this.iPlayEnd = iPlayEnd;
    }

    private class MyThered extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                synchronized (holder) {
                    if (isMove) {
                        Canvas mCanvas = holder.lockCanvas();
                        if (mCanvas != null) {
                            long time = System.currentTimeMillis();
                            mCanvas.drawColor(PixelFormat.TRANSLUCENT, PorterDuff.Mode.CLEAR);
                            if (!isPush) {
                                if (num < endNum - 1) {
                                    setProgresBar(mCanvas, progressX);
                                    if (progressX > fristLinsEndX.get(posiotion)) {
                                        progressX += mLinsRoomWidth4;
                                        posiotion++;
                                    } else {
                                        progressX += mSpeed * mReta;
                                    }
                                    if (posiotion == measureNum) {
                                        progressX = startX;
                                        posiotion = 0;
                                        num++;
                                        index += measureNum;
                                        if (num % Constents.LINE_NUM == 0) {
                                            mStaffView.startMyThered(index);
                                        }
                                    }
                                } else {
                                    if (progressX > fristLinsEndX.get(posiotion)) {
                                        progressX += mLinsRoomWidth4;
                                        posiotion++;
                                    } else {
                                        progressX += mSpeed * mReta;
                                    }
                                    if (posiotion == endMeasureNum) {
                                        //播放结束
                                        index = 0;
                                        num = 0;
                                        progressX = startX;
                                        posiotion = 0;
                                        mStaffView.startMyThered(index);
                                        isMove = false;
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                iPlayEnd.end();
                                            }
                                        });
                                    } else {
                                        setProgresBar(mCanvas, progressX);
                                    }
                                }
                            } else {
                                isPush = false;
                            }
                            try {
                                Thread.sleep(Math.max(0, 30 - (System.currentTimeMillis() - time)));
                                //释放canvas对象，并发送到SurfaceView
                                holder.unlockCanvasAndPost(mCanvas);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        mProgessRectF.top = top + (num % Constents.LINE_NUM) * mLinsRoomWidth30;
        mProgessRectF.right = mLeft + 5;
        mProgessRectF.bottom = bottom + (num % Constents.LINE_NUM) * mLinsRoomWidth30;
        canvas.drawRoundRect(mProgessRectF, 90, 90, mBluePaint);
    }

    /**
     * 设置进度条数据
     *
     * @param mStaffView
     * @param iPlay      五线谱准备完成
     * @param x          第一行定义一个元素开始的位置
     * @param fristEndX  第一行每一小节结束的位置
     * @param measureNum 每一行的数量
     */
    public void setProgressData(StaffView mStaffView, final IPlay iPlay,
                                int linsRoomWidth, float x, List<Float> fristEndX, int measureNum) {
        MyLogUtils.e(TAG, "设置进度条数据");
        if (iPlay == null) return;
        if (mStaffView == null) return;
        if (fristEndX == null || fristEndX.size() == 0) return;
        mLinsRoomWidth30 = linsRoomWidth * 30;
        mLinsRoomWidth4 = linsRoomWidth * 4;
        startX = x;
        posiotion = 0;
        fristLinsEndX.clear();
        fristLinsEndX.addAll(fristEndX);
        index = 0;
        num = 0;
        this.measureNum = 0;
        this.measureNum = measureNum;
        endNum = 0;
        progressX = startX;
        boolean isUp = mStaffView.isUpNote();
        if (isUp) {
            top = mStaffView.getTwoStaff_fiveLins_up() - 15;
        } else {
            top = mStaffView.getTwoStaff_fiveLins_down() - 15;
        }
        if (StaffDataHelper.getInstence().isTowStaff()) {
            bottom = mStaffView.getTwoStaff_fristLins_down() + 15;
        } else {
            if (isUp) {
                bottom = mStaffView.getTwoStaff_fristLins_up() + 15;
            } else {
                bottom = mStaffView.getTwoStaff_fristLins_down() + 15;
            }
        }
        MyLogUtils.e(TAG, "节数" + StaffDataHelper.getInstence().getmFristStaffData().size());
        int line = StaffDataHelper.getInstence().getmFristStaffData().size() / this.measureNum;
        if (line == 1) {
            if (StaffDataHelper.getInstence().getmFristStaffData().size() == this.measureNum) {
                endNum = line;
                endMeasureNum = measureNum;
            } else {
                endMeasureNum = StaffDataHelper.getInstence().getmFristStaffData().size() - measureNum;
                endNum = line + 1;
            }
        } else {
            if (StaffDataHelper.getInstence().getmFristStaffData().size() % measureNum == 0) {
                endNum = line;
                endMeasureNum = measureNum;
            } else {
                endNum = line + 1;
                endMeasureNum = StaffDataHelper.getInstence().getmFristStaffData().size() % measureNum;
            }
        }
        MyLogUtils.e(TAG, "END" + StaffDataHelper.getInstence().getmFristStaffData().size());
        MyLogUtils.e(TAG, "END" + line);
        MyLogUtils.e(TAG, "END" + endMeasureNum);
        mReta = (float) StaffDataHelper.getInstence().getDEFAULT_TIME_NUM() / 100;
        mSpeed = StaffDataHelper.getInstence().getmSpeedLenth() * StaffDataHelper.getInstence().getDivisions() / 20;
        this.mStaffView = mStaffView;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                iPlay.ReadyFinish();
            }
        });
    }

    /**
     * 获取速度
     *
     * @return
     */
    public String getmReta() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return "速度：" + decimalFormat.format(mReta);
    }

    /**
     * 加速
     */
    public String accelerate() {
        mReta += 0.1f;
        if (mReta >= 1.5f) mReta = 1.5f;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return "速度：" + decimalFormat.format(mReta);
    }

    /**
     * 减速
     */
    public String deceleration() {
        mReta -= 0.1f;
        if (mReta <= 0.5f) mReta = 0.5f;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return "速度：" + decimalFormat.format(mReta);
    }

    public void play(boolean isPlay) {
        MyLogUtils.e(TAG, "播放/暂停");
        isMove = isPlay;
        if (myThered == null) {
            myThered = new MyThered();
            myThered.start();
        }
    }


    public void isPush() {
        isPush = true;
    }
}
