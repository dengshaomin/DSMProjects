package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Piano;
import com.yizu.intelligentpiano.bean.PianoKey;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/12/1.
 * Email: chenhuixueba@163.com
 * 绘制键盘
 */

public class KeyView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "PianoKeyView";
    //钢琴键总数目
    private final static int PIANO_NUMS = 88;
    //黑键总数目
    public final static int BLACK_PIANO_KEY_NUMS = 36;
    //白键总数目
    public final static int WHITE_PIANO_KEY_NUMS = 52;

    private Context mContext;
    //定义钢琴键
    private Piano mPiano;
    //布局宽高
    private int mLayoutWidth;
    private int mLayoutHeight;

    //黑白键高度和宽度
    private int mBlackKeyWidth;
    private int mBlackKeyHeight;
    private int mWhiteKeyWidth;
    private int mWhiteKeyHeight;

    private float scaleWidth;
    private float scaleHeight;

    //白键组合
    private ArrayList<PianoKey[]> whitePianoKeys;
    //黑键组合
    private ArrayList<PianoKey[]> blackPianoKeys;
    //黑白键
    private List<PianoKey> allPianoKeys;

    private SurfaceHolder holder;
    private MyThread thread;
    private boolean isDraw = false;
    private Canvas mCanvas;
    private Rect mRect;
    private int press = 0;

    public KeyView(Context context) {
        this(context, null);
    }

    public KeyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        holder = getHolder();
        holder.addCallback(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取布局宽度
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        Drawable whiteKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.white_piano_key);
        Drawable blackKeyDrawable = ContextCompat.getDrawable(mContext, R.drawable.black_piano_key);
        //黑白键的宽高
        int whiteKeyHeight = whiteKeyDrawable.getIntrinsicHeight();
        int whiteKeyWidth = whiteKeyDrawable.getIntrinsicWidth();
        int blackKeyWidth = blackKeyDrawable.getIntrinsicWidth();
        int blackKeyHeight = blackKeyDrawable.getIntrinsicHeight();
        //根据白键的宽高计算比例
        float scale = (float) whiteKeyHeight / whiteKeyWidth;
        MyLogUtils.e(TAG, "" + scale);
        //白键宽度
        mWhiteKeyWidth = (mLayoutWidth + 60) / WHITE_PIANO_KEY_NUMS;
//        计算得出布局的实际高度
        mLayoutHeight = (int) ((mWhiteKeyWidth * scale * 10 + 9) / 10);
        mWhiteKeyHeight = mLayoutHeight;
        scaleHeight = (float) mWhiteKeyHeight / whiteKeyHeight;
        scaleWidth = (float) mWhiteKeyWidth / whiteKeyWidth;
        //计算黑键宽高
        mBlackKeyHeight = (int) (blackKeyHeight * scaleHeight);
        mBlackKeyWidth = (int) (blackKeyWidth * scaleWidth);
        //从新设置布局高度和宽度
        setMeasuredDimension(mLayoutWidth, mLayoutHeight);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mPiano == null) {
            mPiano = new Piano(mContext, mBlackKeyHeight, mBlackKeyWidth,
                    mWhiteKeyHeight, mWhiteKeyWidth, scaleHeight, scaleWidth);
//            //获取白键
            whitePianoKeys = mPiano.getWhitePianoKeys();
//            //获取黑键
            blackPianoKeys = mPiano.getBlackPianoKeys();
            //获取所有按顺序排列的组合
            allPianoKeys = mPiano.getKeyList();
        }
        isDraw = true;
        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                if (isDraw) {
                    synchronized (holder) {
                        mCanvas = null;
                        if (mRect == null) {
                            mCanvas = holder.lockCanvas();
                            mCanvas.drawColor(Color.BLACK);
                            //      初始化白键
                            for (int i = 0; i < whitePianoKeys.size(); i++) {
                                for (PianoKey key : whitePianoKeys.get(i)) {
                                    key.getKeyDrawable().draw(mCanvas);
                                }
                            }
                            //初始化黑键
                            for (int i = 0; i < blackPianoKeys.size(); i++) {
                                for (PianoKey key : blackPianoKeys.get(i)) {
                                    key.getKeyDrawable().draw(mCanvas);
                                }
                            }
                        } else {
                            mCanvas = holder.lockCanvas(mRect);
//                            mCanvas.drawColor(Color.BLACK);
                            allPianoKeys.get(press).getKeyDrawable().draw(mCanvas);
                            if (press < 3) {
                                if (press != 1) {
                                    allPianoKeys.get(1).getKeyDrawable().draw(mCanvas);
                                }
                            } else if (press < 87) {
                                switch ((press - 3) % 12) {
                                    case 0:
                                        allPianoKeys.get(press + 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 2:
                                        allPianoKeys.get(press + 1).getKeyDrawable().draw(mCanvas);
                                        allPianoKeys.get(press - 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 4:
                                        allPianoKeys.get(press - 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 5:
                                        allPianoKeys.get(press + 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 7:
                                        allPianoKeys.get(press + 1).getKeyDrawable().draw(mCanvas);
                                        allPianoKeys.get(press - 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 9:
                                        allPianoKeys.get(press + 1).getKeyDrawable().draw(mCanvas);
                                        allPianoKeys.get(press - 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                    case 11:
                                        allPianoKeys.get(press - 1).getKeyDrawable().draw(mCanvas);
                                        break;
                                }
                            }
                        }
                        if (mCanvas != null) {
                            holder.unlockCanvasAndPost(mCanvas);
                            mCanvas = null;
                        }

                    }
                }
            }
        }
    }

    /**
     * 实现按压效果
     *
     * @param prass
     * @param isPress
     */
    public synchronized void painoKeyPress(int prass, boolean isPress) {
        allPianoKeys.get(prass - 21).getKeyDrawable().setState(isPress ? new int[]{android.R.attr.state_pressed} : new int[]{-android.R.attr.state_pressed});
        mRect = allPianoKeys.get(prass - 21).getKeyDrawable().getBounds();
        press = prass - 21;
        isDraw = true;
    }

    /**
     * 获取白键宽度
     *
     * @return
     */
    public int getmWhiteKeyWidth() {
        return mWhiteKeyWidth;
    }

    /**
     * 获取黑键宽度
     *
     * @return
     */
    public int getmBlackKeyWidth() {
        return mBlackKeyWidth;
    }

    public void onResume() {
        isDraw = true;
    }

    public void onPause() {
        isDraw = false;
    }

    public void onDrestry() {
        isDraw = false;
        if (mCanvas != null) {
            holder.unlockCanvasAndPost(mCanvas);
            mCanvas = null;
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

}
