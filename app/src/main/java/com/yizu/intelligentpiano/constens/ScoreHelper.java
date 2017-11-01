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
    private int totalNodes;
    private int correctNodes;
    private int hasGoneNodes;
    private List<SaveTimeData> correctKeys;
    private List<Integer> physicKeys;
    private ScoreCallBack callback;
    private int realScore;

    public ScoreCallBack getCallback() {
        return callback;
    }

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


    public int getRealScore() {
        return realScore;
    }

    public void setRealScore(int realScore) {
        this.realScore = realScore;
    }

    public void setTotalNode(int totalNode) {
        totalNodes = totalNode;
    }

    public void setCorrectKey(RectF rectF, SaveTimeData saveTimeData, int bottom) {
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (rectF.bottom >= bottom && rectF.top <= bottom) {
            if (!saveTimeData.isPressCorrect()) {
                saveTimeData.setPressCorrect(true);
//                saveTimeData.setPhysicalKey(getPhysicKey(saveTimeData));
                correctKeys.add(saveTimeData);
            }
        } else if(rectF.top > bottom && correctKeys.contains(saveTimeData)){
            hasGoneNodes++;
            saveTimeData.setPressCorrect(false);
            correctKeys.remove(saveTimeData);
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
        }
    }

    public void caCorrectKey(int physicKey, boolean press) {
        if (physicKeys == null) physicKeys = new ArrayList<>();
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (press) {
            if (!physicKeys.contains(physicKey)) {
                physicKeys.add(physicKey);
//                if (callback != null) {
//                    callback.callBack(caRealTimeScores());
//                }
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
        for (SaveTimeData saveTimeData : correctKeys) {
            if (saveTimeData != null && saveTimeData.getPhysicalKey() == physicKey) {
                if (press) {
                    saveTimeData.setHasRecord(true);
                    correctNodes++;
                } else {
                }
            }

        }
    }

    private int caRealTimeScores() {
        if (hasGoneNodes == 0) return 100;
        return (int) ((float) correctNodes / (float) hasGoneNodes * 100);
    }

    public List<SaveTimeData> getCurrentKeys() {
        return correctKeys;

    }

    private Integer getPhysicKey(SaveTimeData saveTimeData) {
        return 31;
    }

    public int caLastScores() {
//        return (int) ((float) correctNodes / (float) totalNodes * 100);
        Log.e("code", "last:" + hasGoneNodes);
        int score = caRealTimeScores();
        score = score > 100 ? 100 : score;
        return 0 < score ? score : 0;
    }

    public void reset() {
        totalNodes = correctNodes = hasGoneNodes = 0;
        correctKeys = new ArrayList<>();
        physicKeys = new ArrayList<>();
    }

    public interface ScoreCallBack {
        public void callBack(int score);
    }
}
