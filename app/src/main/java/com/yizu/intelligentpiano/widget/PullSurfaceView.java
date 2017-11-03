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
import com.yizu.intelligentpiano.constens.IPlayState;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/10/19.
 * All Rights Reserved by YiZu
 */

public class PullSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = "PullView";

    //白键总数目
    private final static int WHITE_PIANO_KEY_NUMS = 52;
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
    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 88;

    private Paint mPaint;
    private Paint resetPaint;
    private RectF mRectF;
    private PrgoressView mPrgoressView;
    private StaffView mStaffView;

    private int mScrollHeight = 0;
//    int num;

    private boolean playState;

    private int staffMove = 0;
    private boolean isMoveStaff = false;

    List<Integer> fristSingLenth;
    private Handler handler = new Handler(Looper.getMainLooper());

    public PullSurfaceView(Context context) {
        this(context, null);
    }

    public PullSurfaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullSurfaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        holder = getHolder();//获取SurfaceHolder对象，同时指定callback
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.blue));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        resetPaint = new Paint();
        resetPaint.setColor(getResources().getColor(R.color.red));
        resetPaint.setStyle(Paint.Style.FILL);
        resetPaint.setAntiAlias(true);
        mRectF = new RectF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyLogUtils.e(TAG, "onMeasure");
        mLayoutWith = MeasureSpec.getSize(widthMeasureSpec);
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        MyLogUtils.e(TAG, "mLayoutHeight：" + mLayoutHeight);
//        if (mAttributess == null) {
//            return;
//        }
//        //一个小结的duration总数量
//        num = Integer.valueOf(mAttributess.getDivisions()) * Integer.valueOf(mAttributess.getTime().getBeats());
////        计算每个duration的距离
//        mSpeedLenth = mLayoutHeight / num;
//        mSpeedTime = (60 * 1000 / (DEFAULT_TIME_NUM * Integer.valueOf(mAttributess.getDivisions())));
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
        Attributess attributess = mStaffView.getmAttributess();
        if (attributess == null) return;
        if (mStaffView.getPullData() == null) return;
        if (mProgessView == null) return;
        if (mStaffView.getFristSingLenth() == null) return;
        this.mStaffView = mStaffView;
        this.mPrgoressView = mProgessView;
        this.mPianoKeyView = mPianoKeyView;
        fristSingLenth = mStaffView.getFristSingLenth();
        mAttributess = attributess;
        //每个duration多少像素(十分之一)
        mSpeedLenth = mStaffView.getmSpeedLenth();
        //每个duration多少毫秒（十分之一）
        mSpeedTime = mStaffView.getmSpeedTime();
        //默认每分钟88拍
        DEFAULT_TIME_NUM = mStaffView.getTimes();
        mData = mStaffView.getPullData();
        ScoreHelper.getInstance().setTotalNode(mData.size());
//        invalidate();
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


    private void caAllPosition() {
        int size = mData.size();

        for (int i = 0; i < size; i++) {
            List<SaveTimeData> frist_hide = mData.get(i).getFrist();
            List<SaveTimeData> second_hide = mData.get(i).getSecond();
            boolean lastNodeFlag = frist_hide.size() > second_hide.size();
            for (int j = 0; j < frist_hide.size(); j++) {
                if (j == 0) {
                    SaveTimeData data = frist_hide.get(0);
                    if (frist_hide.get(0).isRest()) {
                        int botom = mScrollHeight - data.getmAddDuration() * mSpeedLenth;
                        judgeStaffBeat(botom, i);
                    } else {
                        int botom = mScrollHeight - data.getmAddDuration() * mSpeedLenth;
                        judgeStaffBeat(botom, i);
                    }
                }
                calculationPosiotion(canvas, frist_hide.get(j), true, (i == size - 1 &&
                        lastNodeFlag) ? (j ==
                        frist_hide.size() - 1 ? true : false) : false);
                frist_hide.get(j).setLastNode(false);

            }
            for (int j = 0; j < second_hide.size(); j++) {
                calculationPosiotion(canvas, second_hide.get(j), false, (i == size - 1
                        && !lastNodeFlag) ? (j == second_hide.size() - 1 ? true : false) : false);
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
        if (mysurfaceviewThread == null) {
            mysurfaceviewThread = new MysurfaceviewThread();
            mysurfaceviewThread.start();
            playState = true;
        } else {
            playState = !playState;
        }
    }


//    private Map<String, Integer> physicKeys;

    SurfaceHolder holder;
    MysurfaceviewThread mysurfaceviewThread;


    private void allFinish() {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当SurfaceView被创建时，将画图Thread启动起来。
        if (mysurfaceviewThread != null) {
            playState = true;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当SurfaceView被销毁时，释放资源。
        playState = false;
    }


    private Canvas canvas;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mysurfaceviewThread != null) {
            mysurfaceviewThread.interrupt();
            mysurfaceviewThread = null;
        }
        playState = !playState;
    }

    public void onResume() {
        int a = 1;
    }

    public void onPause() {
        playState = false;
    }

    private IPlayState iPlayState;

    public void setiPlayState(IPlayState iPlayState) {
        this.iPlayState = iPlayState;
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
            // TODO Auto-generated method stub
            super.run();
//            Looper.prepare();
            SurfaceHolder surfaceHolder = holder;
            while (true) {
                if (playState) {
                    synchronized (surfaceHolder) {
                        //锁定canvas
                        try {
                            canvas = surfaceHolder.lockCanvas();
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
                                        move(frist_hide.get(j), true, (i == size - 1 &&
                                                lastNodeFlag) ? (j ==
                                                frist_hide.size() - 1 ? true : false) : false);

                                    }
                                    for (int j = 0; j < second_hide.size(); j++) {
                                        move(second_hide.get(j), false, (i == size - 1
                                                && !lastNodeFlag) ? (j == second_hide.size() - 1 ? true : false) : false);
                                    }
                                }
//                                Integer most = 0;
//                                for (String key : physicKeys.keySet()) {
//                                    if (physicKeys.get(key) > most) {
//                                        most = physicKeys.get(key);
//                                    }
//                                }
//                                for (String key : physicKeys.keySet()) {
//                                    if (physicKeys.get(key) == most) {
//                                        most = physicKeys.get(key);
//                                        Log.e("code", key + "==" + most);
//                                        break;
//                                    }
//                                }

                                //释放canvas对象，并发送到SurfaceView
                                if (canvas != null) {
                                    surfaceHolder.unlockCanvasAndPost(canvas);
                                }
                                Thread.sleep(speed);
                            }
                        } catch (Exception e) {

                        } finally {
//                            if (canvas != null ) {
//                                surfaceHolder.unlockCanvasAndPost(canvas);
//                            }
                        }

                    }
//                    try {
//                        Thread.sleep(mSpeedTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mScrollHeight += mSpeedLenth;
//                    if (isMoveStaff) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                staffMove += mSpeedLenth;
//                                mStaffView.remove(staffMove);
//                            }
//                        });
//                    }
                }
            }
        }

        public void end() {

        }
    }

    /**
     * 计算每个音符的位置
     *
     * @param canvas
     * @param saveTimeData
     * @return
     */
    private void calculationPosiotion(Canvas canvas, SaveTimeData saveTimeData, boolean firstLine, boolean lastNode) {
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
        }
//            if (physicKeys == null) {
//                physicKeys = new HashMap<>();
//            }
//            physicKeys.put(keyNum + "", physicKeys.containsKey(keyNum + "") ? (physicKeys.get(keyNum+"") + 1) : 1);
        saveTimeData.setPhysicalKey(keyNum);
        mRectF.top = mScrollHeight - (saveTimeData.getmAddDuration() + saveTimeData.getDuration()) * mSpeedLenth;
        mRectF.bottom = mScrollHeight - saveTimeData.getmAddDuration() * mSpeedLenth;

        saveTimeData.setTop(mRectF.top);
        saveTimeData.setBottom(mRectF.bottom);
        saveTimeData.setLeft(mRectF.left);
        saveTimeData.setRight(mRectF.right);
        saveTimeData.setHasCac(true);
    }

    private void move(SaveTimeData saveTimeData, boolean firstLine, boolean lastNode) {
        saveTimeData.setTop(saveTimeData.getTop() + speed * moveDistance);
        saveTimeData.setBottom(saveTimeData.getBottom() + speed * moveDistance);
        mRectF.left = saveTimeData.getLeft();
        mRectF.top = saveTimeData.getTop();
        mRectF.right = saveTimeData.getRight();
        mRectF.bottom = saveTimeData.getBottom();
        canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, mPaint);
        ScoreHelper.getInstance().setCorrectKey(mRectF, saveTimeData, getBottom());
        if (firstLine && saveTimeData.getArriveBottomState() == 1) {
            //该数据对应的音符第一次达到pullview底部

        }
        if (mRectF.bottom > getTop() && mRectF.top <= getBottom()) {
            canvas.drawRoundRect(mRectF, mWhiteKeyWidth / 4, mWhiteKeyWidth / 4, saveTimeData.isRest() ?
                    resetPaint : mPaint);
        }
        if (saveTimeData.isLastNode() && mRectF.top > getBottom()) {
            //谱子结束
            if (mysurfaceviewThread != null) {
                mysurfaceviewThread.end();
                mysurfaceviewThread.interrupt();
                mysurfaceviewThread = null;
            }
            playState = !playState;
            if (iPlayState != null) {
                iPlayState.end();
            }
            //重新计算坐标，以方便用户点击再次开始
            caAllPosition();

            return;
        }
    }

    //每次刷新移动的距离
    private final int moveDistance = 5;
    //刷新速度
    private final int speed = 5;

    /**
     * 判断staff是否校准
     *
     * @param botom 休止符号
     * @param i
     */
    private void judgeStaffBeat(int botom, int i) {
        if (botom - mLayoutHeight >= 0 && botom - mLayoutHeight < mSpeedLenth) {
            MyLogUtils.e(TAG, "进度条更新");
            if (i == 0) {
                MyLogUtils.e(TAG, "进度条显示");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        isMoveStaff = true;
                        mPrgoressView.setIsShow(true);
                    }
                });
            } else {
                staffMove = fristSingLenth.get(i);
                MyLogUtils.e(TAG, "staffMove" + staffMove);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mStaffView.remove(staffMove);
                    }
                });
            }
        }
    }

}
