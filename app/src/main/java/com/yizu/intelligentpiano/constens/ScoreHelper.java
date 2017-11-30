package com.yizu.intelligentpiano.constens;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
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
    private List<SaveTimeData> correctKeys;
    //正在按的钢琴键
    private List<Integer> physicKeys;
    private ScoreCallBack callback;
    //当前分数
    private int realScore = 0;

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
     * @param rectF
     * @param saveTimeData
     * @param bottom
     */
    public synchronized void setCorrectKey(RectF rectF, SaveTimeData saveTimeData, int bottom) {
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (rectF.bottom >= bottom && rectF.top <= bottom) {
            //设置当前音符的位置的状态。0:未到弹奏 1：开始弹奏 2.结束弹奏
            if (saveTimeData.getArriveBottomState() == 0) {
                saveTimeData.setArriveBottomState(1);
                //不计算休止符号
                if (!saveTimeData.isRest()) {
                    correctKeys.add(saveTimeData);
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
        } else if (rectF.top > bottom && correctKeys.contains(saveTimeData)) {
            correctKeys.remove(saveTimeData);
        }
    }

    /**
     * 当前按键
     *
     * @param physicKey
     */
    public synchronized void caCorrectKey(int physicKey) {
        if (correctKeys == null) return;
        if (physicKeys == null) physicKeys = new ArrayList<>();
        if (!physicKeys.contains(physicKey)) {
            physicKeys.add(physicKey);
        }
    }

    /**
     * 计算打分
     *
     * @return 返回当前得分
     */
    private int caRealTimeScores() {
        if (hasGoneNodes == 0) return 0;
        for (SaveTimeData saveTimeData : correctKeys) {
            if (physicKeys == null) physicKeys = new ArrayList<>();
            for (int i = 0; i < physicKeys.size(); i++) {
                if (physicKeys.get(i) == saveTimeData.getPhysicalKey()) {
                    correctNodes++;
                    physicKeys.remove(i);
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
        if (correctKeys != null) {
            correctKeys.clear();
            correctKeys = null;
        }
        if (physicKeys != null) physicKeys.clear();
        realScore = 0;
        hasGoneNodes = 0;
        correctNodes = 0;
    }

    public interface ScoreCallBack {
        void callBack(int score);
    }
}
