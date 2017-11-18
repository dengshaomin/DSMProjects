package com.yizu.intelligentpiano.constens;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yizu.intelligentpiano.bean.SaveTimeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengshaomin on 2017/10/30.
 */

public class ScoreHelper {

    private static ScoreHelper scoreHelper;
    private int correctNodes;
    private int hasGoneNodes;
    private List<SaveTimeData> correctKeys;
    private List<Integer> physicKeys;
    private ScoreCallBack callback;
    private int realScore;

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

    public void setCorrectKey(RectF rectF, SaveTimeData saveTimeData, int bottom) {
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (rectF.bottom >= bottom && rectF.top <= bottom) {
            if (saveTimeData.getArriveBottomState() == 0) {
                saveTimeData.setArriveBottomState(1);
                correctKeys.add(saveTimeData);
            } else if (saveTimeData.getArriveBottomState() == 1) {
                saveTimeData.setArriveBottomState(2);
            }
            caRealTimeScores();
            int score = caRealTimeScores();
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
            hasGoneNodes++;
//            Log.e("code", hasGoneNodes + "");
            correctKeys.remove(saveTimeData);
        }
    }

    public void caCorrectKey(int physicKey, boolean press) {
        if (physicKeys == null) physicKeys = new ArrayList<>();
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (press) {
            if (!physicKeys.contains(physicKey)) {
                physicKeys.add(physicKey);
            }
        } else {
            if (physicKeys.contains(physicKey)) {
                for (int i = 0; i < physicKeys.size(); i++) {
                    if (physicKey == physicKeys.get(i)) {
                        physicKeys.remove(i);
                        break;
                    }
                }
            }
        }

    }

    private int caRealTimeScores() {
        if (hasGoneNodes == 0) return 100;
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
        return (int) ((float) correctNodes / (float) hasGoneNodes * 100);
    }

    /**
     * 谱子结束，初始化打分
     */
    public void initData() {
        if (correctKeys != null) correctKeys.clear();
        if (physicKeys != null) physicKeys.clear();
        realScore = 0;
    }

    public interface ScoreCallBack {
        void callBack(int score);
    }
}
