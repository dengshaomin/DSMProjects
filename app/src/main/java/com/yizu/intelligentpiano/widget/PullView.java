package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLContinuousView;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.PullBack;
import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.helper.ScoreHelper;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

    private List<PullData> mData = new ArrayList<>();

    //    //每个duration多少像素
    private float mSpeedLenth = 0;

    private float mReta = 0.8f;

    private GLPaint mYellowPaint;

    private GLPaint mBackgroundPaint;

    private RectF mRectF;

//    SurfaceHolder holder;


    private IPlayEnd iPlayEnd;


    //是否播放五线谱
    private boolean isPlay = false;


    //用来保存瀑布流灰色背景
    private List<PullBack> mBackList = new ArrayList<>();

    //瀑布流从第几小节开始
    private int index = 0;

    //瀑布流下落得距离
    private float move = 0;

    private GLPaint mPaint;

    private Handler handler = new Handler(Looper.getMainLooper());

    private float mLenth;//100拍的长度
    //一小节的druction
    private int measureDurationNum = 16;
    //每小节的拍数
    private int beat = 4;

    /****
     * *****************************************
     * 按键动画效果
     */
    private GLPaint mPaints;
    private List<Bubble> bubbleList;
    private int maxRadius = 10;
    private int maxBubble = 15;
    private Random mRandom;
    //    private Map<Integer, Float> map = new HashMap<>();
    private int add = 0;
//    private List<Integer> key = new ArrayList<>();

    public PullView(Context context) {
        this(context, null);
    }

    public PullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        //整个界面透明
//        holder.setFormat(PixelFormat.TRANSPARENT);
//        setZOrderOnTop(false);

        mYellowPaint = new GLPaint();
        mYellowPaint.setStyle(Paint.Style.FILL);
        mYellowPaint.setColor(getResources().getColor(R.color.yellow));
        mRectF = new RectF();

        mPaint = new GLPaint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.blue));
//        mPaint.setAntiAlias(true);

        mBackgroundPaint = new GLPaint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(getResources().getColor(R.color.pullcolor));
//        mBackgroundPaint.setAntiAlias(true);

        bubbleList = new ArrayList<>();
        mPaints = new GLPaint();
        mPaints.setStyle(Paint.Style.FILL);
//        mPaints.setAntiAlias(true);
        mPaints.setColor(Color.YELLOW);
        mRandom = new Random();

//        ScoreHelper.getInstance().setBubble(new IBubble() {
//            @Override
//            public void pullAdd(int keyId, float x) {
//                map.put(keyId, x);
//            }
//
//            @Override
//            public void pullRemove(int keyId, float x) {
//                map.remove(keyId);
//                if (map.size() == 0) key.clear();
//            }
//
//            @Override
//            public void keyAdd(int keyId) {
//                if (map.size() != 0) key.add(keyId);
//            }
//
//            @Override
//            public void keyRemove(int keyId) {
//                if (key.contains(keyId)) key.remove(key.indexOf(keyId));
//            }
//
//            @Override
//            public void clear() {
//                map.clear();
//                key.clear();
//            }
//        });
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        if (isPlay) {
            long time = System.currentTimeMillis();
            move += mLenth * mReta;
            //显示瀑布流就绘制
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
            for (int k = 0; k < mBackList.size(); k++) {
                //引导条
                mRectF.left = mBackList.get(k).getLeft();
                mRectF.top = 0;
                mRectF.right = mBackList.get(k).getRight();
                mRectF.bottom = mLayoutHeight;
                canvas.drawRect(mRectF, mBackgroundPaint);
            }
            initCircle();
            BitSet data = ScoreHelper.getInstance().getKey();
            List<SaveTimeData> map = ScoreHelper.getInstance().getCorrectKeys();
            for (int i = 0; i < map.size(); i++) {
                SaveTimeData saveTimeData = map.get(i);
                if (data.get(saveTimeData.getPhysicalKey())) {
                    for (int k = 0; k < bubbleList.size(); k++) {
                        canvas.drawCircle(bubbleList.get(k).getCenterX() + saveTimeData.getLeft() + 15,
                                bubbleList.get(k).getCenterY(),
                                bubbleList.get(k).getRadius(), mPaints);
                    }
                }
            }
            try {
//                MyLogUtils.e(TAG, (System.currentTimeMillis() - time) + "");
                Thread.sleep((long) Math.max(0f, 20 - (System.currentTimeMillis() - time)));
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
        mSpeedLenth = (float) mLayoutHeight / measureDurationNum;
        mLenth = (float) mLayoutHeight / (beat * 30);
    }


    /**
     * 设置瀑布流数据
     */
    public void setPullData(PianoKeyView mPianoKeyView,
                            final IPlay iPlay) {
        MyLogUtils.e(TAG, "初始化第一条瀑布流");
        if (mPianoKeyView == null) return;
        mAttributess = null;
        mAttributess = StaffDataHelper.getInstence().getmAttributess();
        if (mAttributess == null) return;
        initAllData();
        measureDurationNum = StaffDataHelper.getInstence().getMeasureDurationNum();
        beat = StaffDataHelper.getInstence().getBeats();
        mSpeedLenth = (float) mLayoutHeight / measureDurationNum;
        mLenth = (float) mLayoutHeight / (beat * 30);
        mData.clear();
        mData.addAll(StaffDataHelper.getInstence().getPullData());

        if (mData == null) return;
        if (mWhiteKeyWidth == 0) {
            mWhiteKeyWidth = mPianoKeyView.getmWhiteKeyWidth();
            mBlackKeyWidth = mPianoKeyView.getmBlackKeyWidth();
            maxRadius = mBlackKeyWidth / 5;
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
        isPlay = isplay;
        if (isplay) {
            invalidate();
        }
    }

    public void resetPullView() {
        isPlay = false;
        index = 0;
        move = 0;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
            int keys = (octave - 1) * 12 + 23;
            int blackLenth = mBlackKeyWidth / 2;
            if (!saveTimeData.isRest()) {
                if (octave == 0) {
                    switch (saveTimeData.getStep()) {
                        case "A":
                            if (black == 1) {
                                keyNum = 22;
                                mRectF.left = mWhiteKeyWidth - blackLenth + 7;
                                mRectF.right = mWhiteKeyWidth + blackLenth + 7;
                            } else {
                                keyNum = 21;
                                mRectF.left = 0;
                                mRectF.right = mWhiteKeyWidth - blackLenth + 7;
                            }
                            break;
                        case "B":
                            if (black == -1) {
                                keyNum = 22;
                                mRectF.left = mWhiteKeyWidth - blackLenth + 7;
                                mRectF.right = mWhiteKeyWidth + blackLenth + 7;
                            } else {
                                keyNum = 23;
                                mRectF.left = mWhiteKeyWidth + blackLenth + 7;
                                mRectF.right = mWhiteKeyWidth * 2;
                            }
                            break;
                    }
                } else {
                    switch (saveTimeData.getStep()) {
                        case "C":
                            if (black == 1) {
                                keyNum = keys + 2;
                                mRectF.left = mWhiteKeyWidth * 3 - blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 3 + blackLenth - 7 + num;
                            } else {
                                keyNum = keys + 1;
                                mRectF.left = mWhiteKeyWidth * 2 + num;
                                mRectF.right = mWhiteKeyWidth * 3 - blackLenth - 7 + num;
                            }
                            break;
                        case "D":
                            if (black == 1) {
                                keyNum = keys + 4;
                                mRectF.left = mWhiteKeyWidth * 4 - blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 4 + blackLenth + 7 + num;
                            } else if (black == -1) {
                                keyNum = keys + 2;
                                mRectF.left = mWhiteKeyWidth * 3 - blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 3 + blackLenth - 7 + num;
                            } else {
                                keyNum = keys + 3;
                                mRectF.left = mWhiteKeyWidth * 3 + blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 4 - blackLenth + 7 + num;
                            }
                            break;
                        case "E":
                            if (black == -1) {
                                keyNum = keys + 4;
                                mRectF.left = mWhiteKeyWidth * 4 - blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 4 + blackLenth + 7 + num;
                            } else {
                                keyNum = keys + 5;
                                mRectF.left = mWhiteKeyWidth * 4 + blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 5 + num;
                            }
                            break;
                        case "F":
                            if (black == 1) {
                                keyNum = keys + 7;
                                mRectF.left = mWhiteKeyWidth * 6 - blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 6 + blackLenth - 7 + num;
                            } else {
                                keyNum = keys + 6;
                                mRectF.left = mWhiteKeyWidth * 5 + num;
                                mRectF.right = mWhiteKeyWidth * 6 - blackLenth - 7 + num;
                            }
                            break;
                        case "G":
                            if (black == 1) {
                                keyNum = keys + 9;
                                mRectF.left = mWhiteKeyWidth * 7 - blackLenth + num;
                                mRectF.right = mWhiteKeyWidth * 7 + blackLenth + num;
                            } else if (black == -1) {
                                keyNum = keys + 7;
                                mRectF.left = mWhiteKeyWidth * 6 - blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 6 + blackLenth - 7 + num;
                            } else {
                                keyNum = keys + 8;
                                mRectF.left = mWhiteKeyWidth * 6 + blackLenth - 7 + num;
                                mRectF.right = mWhiteKeyWidth * 7 - blackLenth + num;
                            }
                            break;
                        case "A":
                            if (black == 1) {
                                keyNum = keys + 11;
                                mRectF.left = mWhiteKeyWidth * 8 - blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 8 + blackLenth + 7 + num;
                            } else if (black == -1) {
                                keyNum = keys + 9;
                                mRectF.left = mWhiteKeyWidth * 7 - blackLenth + num;
                                mRectF.right = mWhiteKeyWidth * 7 + blackLenth + num;
                            } else {
                                keyNum = keys + 10;
                                mRectF.left = mWhiteKeyWidth * 7 + blackLenth + num;
                                mRectF.right = mWhiteKeyWidth * 8 - blackLenth + 7 + num;
                            }
                            break;
                        case "B":
                            if (black == -1) {
                                keyNum = keys + 11;
                                mRectF.left = mWhiteKeyWidth * 8 - blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 8 + blackLenth + 7 + num;
                            } else {
                                keyNum = keys + 12;
                                mRectF.left = mWhiteKeyWidth * 8 + blackLenth + 7 + num;
                                mRectF.right = mWhiteKeyWidth * 9 + num;
                            }
                            break;
                    }
                }
            }
            saveTimeData.setPhysicalKey(keyNum);
            mRectF.top = 0 - (saveTimeData.getmAddDuration() + saveTimeData.getDuration()) * mSpeedLenth;
            mRectF.bottom = 0 - saveTimeData.getmAddDuration() * mSpeedLenth;
            saveTimeData.setTop(mRectF.top-(saveTimeData.isTie() ? 30 : 0));
            saveTimeData.setBottom(mRectF.bottom);
            saveTimeData.setLeft(mRectF.left - 23);
            saveTimeData.setRight(mRectF.right - 27);
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
        ScoreHelper.getInstance().setCorrectKey(isFrist, j, mRectF, saveTimeData, mLayoutHeight);
        if (isFrist && saveTimeData.getArriveBottomState() == 1) {
            //该数据对应的音符第一次达到pullview底部
            if (j == 0) {
                index = i;
            }
        }
        if (mRectF.bottom > 0 && mRectF.top < mLayoutHeight) {
            if (!saveTimeData.isRest()) {
                canvas.drawEllipse(mRectF, isFrist ? mYellowPaint : mPaint);
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
        //是否播放五线谱
        isPlay = false;
        //是否移动五线谱
        index = 0;
        move = 0;
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
        return "速度：" + decimalFormat.format(mReta);
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
        return "速度：" + decimalFormat.format(mReta);
    }


    public String getmReta() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return "速度：" + decimalFormat.format(mReta);
    }


    /**
     * 用来计算圆形气泡
     */
    private void initCircle() {
        if (add == 0) {
            Bubble bubble = new Bubble();
            bubble.setRadius(maxRadius);
            bubble.setCenterX(0);
            bubble.setCenterY(mLayoutHeight);
            bubble.setDirection(mRandom.nextBoolean());
            if (maxBubble > bubbleList.size()) bubbleList.add(bubble);
        }
        add++;
        if (add > 10) add = 0;
        for (int i = 0; i < bubbleList.size(); i++) {
            float x = bubbleList.get(i).getCenterX();
            float y = bubbleList.get(i).getCenterY();
            float radius = bubbleList.get(i).getRadius();
            x += (bubbleList.get(i).isDirection() ? -1 : 1) * 0.5;
            y -= mRandom.nextFloat() * 3;
            radius -= 0.1f;
            bubbleList.get(i).setCenterX(x);
            bubbleList.get(i).setCenterY(y);
            bubbleList.get(i).setRadius(radius);
        }
        Iterator it = bubbleList.iterator();
        while (it.hasNext()) {
            Bubble bubble1 = (Bubble) it.next();
            if (bubble1.getRadius() < 0) {
                it.remove();
            } else if (bubble1.getCenterY() < 0) {
                it.remove();
            }
        }
    }

    /**
     * 存储圆形的数据
     */
    class Bubble {
        private float centerX;
        private float centerY;
        private float radius;
        //方向，向左还是向右
        private boolean direction;

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public boolean isDirection() {
            return direction;
        }

        public void setDirection(boolean direction) {
            this.direction = direction;
        }
    }
}
