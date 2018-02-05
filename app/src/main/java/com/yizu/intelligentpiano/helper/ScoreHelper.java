package com.yizu.intelligentpiano.helper;

import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;

import com.yizu.intelligentpiano.bean.SaveTimeData;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dengshaomin on 2017/10/30.
 */

public class ScoreHelper {
    private static final String TAG = "ScoreHelper";
    private static ScoreHelper scoreHelper;
    //当前按键正确的总数量
    private int correctNodes = 0;
    //当前的总的音符数量
    private int hasGoneNodes = 0;
    //可以弹奏的音符
    private List<SaveTimeData> correctKeys = new ArrayList<>();
    //保存midi键盘
    private List<Integer> physicKeys = new ArrayList<>();
    //绘制动画
    private List<SaveTimeData> map = new ArrayList<>();
//    private List<Integer> key = new ArrayList<>();
    private BitSet key = new BitSet(109);
    private ScoreCallBack callback;
    //当前分数
    private int realScore = 0;
//    private IBubble iBubble;

    public void setCallback(ScoreCallBack callback) {
        this.callback = callback;
    }

    public static ScoreHelper getInstance() {
        if (scoreHelper == null) {
            synchronized (ScoreHelper.class) {
                if (scoreHelper == null) {
                    scoreHelper = new ScoreHelper();
                }
            }
        }
        return scoreHelper;
    }

    /**
     * 设置当前
     *
     * @param isFrist
     * @param j
     * @param rectF
     * @param saveTimeData
     * @param bottom
     */
    public synchronized void setCorrectKey(boolean isFrist, int j, RectF rectF,
                                           SaveTimeData saveTimeData, int bottom) {
        if (rectF.bottom >= bottom && rectF.top <= bottom) {
            //设置当前音符的位置的状态。0:未到弹奏 1：开始弹奏 2.结束弹奏
            if (saveTimeData.getArriveBottomState() == 0) {
                if (isFrist && j == 0) {
                    map.clear();
                }
                saveTimeData.setArriveBottomState(1);
                //不计算休止符号
                if (!saveTimeData.isRest()) {
                    correctKeys.add(saveTimeData);
                    map.add(saveTimeData);
                    hasGoneNodes++;
                }
            } else if (saveTimeData.getArriveBottomState() == 1) {
                saveTimeData.setArriveBottomState(2);
            }
//            将当前打分计算出来
            int score = caRealTimeScores();
            if (score > 100) score = 100;
            if (score != realScore) {
                realScore = score;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.callBack(realScore);
                        }
                    }
                });
            }
        } else if (rectF.top > bottom && !saveTimeData.isRest()) {
            if (map.contains(saveTimeData))map.remove(saveTimeData);
            if (correctKeys.contains(saveTimeData))correctKeys.remove(saveTimeData);
        }
    }

    /**
     * 当前按键
     *
     * @param physicKey
     */
    public synchronized void caCorrectKey(int physicKey,boolean isPrass) {
        if (isPrass&&!physicKeys.contains(physicKey)) {
            physicKeys.add(physicKey);
        }
        key.set(physicKey,isPrass);
    }

    /**
     * 计算打分
     *
     * @return 返回当前得分
     */
    private int caRealTimeScores() {
        if (hasGoneNodes == 0) return 0;
        Iterator it = correctKeys.iterator();
        while (it.hasNext()) {
            SaveTimeData saveTimeData = (SaveTimeData) it.next();
            for (int i = 0; i < physicKeys.size(); i++) {
                if (physicKeys.get(i) == saveTimeData.getPhysicalKey()) {
                    correctNodes++;
                    physicKeys.remove(i);
                    it.remove();
                    break;
                }
            }
        }
        return correctNodes * 100 / hasGoneNodes;
    }

    /**
     * 谱子结束，初始化打分
     */
    public void initData() {
        correctKeys.clear();
        map.clear();
        physicKeys.clear();
        key.clear();
        realScore = 0;
        hasGoneNodes = 0;
        correctNodes = 0;
    }

    public interface ScoreCallBack {
        void callBack(int score);
    }

    public List<SaveTimeData> getCorrectKeys() {
        return map;
    }

    public BitSet getKey() {
        return key;
    }
}
