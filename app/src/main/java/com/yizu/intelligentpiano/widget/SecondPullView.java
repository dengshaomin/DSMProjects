package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.PullBack;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/11/29.
 * Email: chenhuixueba@163.com
 * 单独绘制第二条五线谱的瀑布流
 */

public class SecondPullView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = "SecondPullView";
    private MyThread thread;
    SurfaceHolder holder;
    private int mLayoutWith;
    private int mLayoutHeight;
    //是否播放五线谱
    private boolean isPlay = false;

    private Canvas mCanvas;
    private Paint mPaint;
    private RectF mRectF;
    private Paint mBackgroundPaint;

    private boolean isShowPullView = true;

    //瀑布流下落得距离
    private float move = 0;
    //瀑布流数据
    private List<PullData> mData;
    //用来保存瀑布流灰色背景
    private List<PullBack> mBackList = new ArrayList<>();

    private int startIndex = 0;
    private int stopIndex = 0;
    //白键宽度
    private int mWhiteKeyWidth;
    //用来保存开始计算的时间（毫秒数）
    long time = 0;

    /**************************************/
    public SecondPullView(Context context) {
        this(context, null);
    }

    public SecondPullView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondPullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setZOrderOnTop(true);
        holder = getHolder();//获取SurfaceHolder对象，同时指定callback
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.blue));
        mPaint.setAntiAlias(true);

        mRectF = new RectF();

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(getResources().getColor(R.color.pullcolor));
        mBackgroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWith = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
// 当SurfaceView被创建时，将画图Thread启动起来。
        if (thread != null) {
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


    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                if (isPlay) {
                    isPlay = false;
                    synchronized (holder) {
                        mCanvas = holder.lockCanvas();
                        if (mCanvas != null) {
                            //绘制第二条瀑布流
                            time = System.currentTimeMillis();
                            //将画布设置成透明
                            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            //如果显示瀑布流
                            if (isShowPullView && mData != null) {
                                mBackList.clear();
                                for (int i = startIndex; i < stopIndex; i++) {
                                    List<SaveTimeData> second_hide = mData.get(i).getSecond();
                                    for (int j = 0; j < second_hide.size(); j++) {
                                        move(mCanvas, second_hide.get(j), i, j);
                                    }
                                }
//                                for (int k = 0; k < mBackList.size(); k++) {
//                                    //引导条
//                                    mRectF.left = mBackList.get(k).getLeft();
//                                    mRectF.top = 0;
//                                    mRectF.right = mBackList.get(k).getRight();
//                                    mRectF.bottom = mLayoutHeight;
//                                    mCanvas.drawRect(mRectF, mBackgroundPaint);
//                                }
                                time = System.currentTimeMillis() - time;
                                MyLogUtils.e(TAG, "time：" + time);
                            }
                            //
                            try {
                                //释放canvas对象，并发送到SurfaceView
                                holder.unlockCanvasAndPost(mCanvas);
                                mCanvas = null;
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
     * *****************外部方法***************************
     *
     * @param data
     * @param mWhiteKeyWidth
     */
    //设置第二条瀑布流的数据
    public void setData(List<PullData> data, int mWhiteKeyWidth) {
        MyLogUtils.e(TAG, "初始化第二条瀑布流");
        move = 0;
        isPlay = true;
        mData = data;
        startIndex = 0;
        stopIndex = 0;
        this.mWhiteKeyWidth = mWhiteKeyWidth;
        //开启线程
        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }
    }

    public void onResume() {
        isPlay = true;
    }

    public void onPause() {
        isPlay = false;
    }

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

    /**
     * 是否显示瀑布流
     *
     * @param isShowPull
     */
    public void isShow(boolean isShowPull) {
        isShowPullView = isShowPull;
    }

    public void setMove(float move, int index, int size) {
        startIndex = index;
        stopIndex = size;
        this.move = move;
        isPlay = true;
    }

    /**
     * 移动无线谱
     *
     * @param canvas
     * @param saveTimeData
     * @param i
     * @param j
     */
    private void move(Canvas canvas, final SaveTimeData saveTimeData, final int i, final int j) {
        mRectF.left = saveTimeData.getLeft();
        mRectF.top = saveTimeData.getTop() + move;
        mRectF.right = saveTimeData.getRight();
        mRectF.bottom = saveTimeData.getBottom() + move;
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, mLayoutHeight);
        if (mRectF.bottom > 0 && mRectF.top < mLayoutHeight) {
            if (!saveTimeData.isRest()) {
                canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, mPaint);
                //保存引导条
                boolean isSave = false;
                for (int k = 0; k < mBackList.size(); k++) {
                    if (mBackList.get(k).getLeft() == mRectF.left) {
                        isSave = true;
                        return;
                    }
                }
                if (!isSave) mBackList.add(new PullBack(mRectF.left, mRectF.right));
            }
        }
    }


    public void resetPullView() {
        setMove(move, 0, 0);
        mData = null;
        isPlay = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        endRefreshCanvas();
    }

    /**
     * 绘制结束刷新画布
     */
    private Canvas canvas;

    public void endRefreshCanvas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SurfaceHolder surfaceHolder = holder;
                synchronized (surfaceHolder) {
                    //锁定canvas
                    try {
                        if (canvas == null)
                            canvas = surfaceHolder.lockCanvas();
                        //canvas 执行一系列画的动作
                        if (canvas != null) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    /**
     * 播放
     *
     * @param isplay
     */
    public void play(boolean isplay) {
        isPlay = isplay;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (isPlay) {
            thread = new MyThread();
            thread.start();
        }
    }
}
