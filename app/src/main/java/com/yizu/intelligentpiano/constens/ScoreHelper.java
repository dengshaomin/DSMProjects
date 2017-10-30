package com.yizu.intelligentpiano.constens;

import android.content.Context;
import android.graphics.RectF;

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


    private List<SaveTimeData> correctKeys;
    private List<Integer> physicKeys;

    public void setTotalNode(int totalNode) {
        totalNodes = totalNode;
    }

    public void setCorrectKey(RectF rectF, SaveTimeData saveTimeData, int bottom) {
        if (correctKeys == null) correctKeys = new ArrayList<>();
        if (rectF.bottom >= bottom && rectF.top <= bottom) {
            if (!saveTimeData.isPressCorrect()) {
                saveTimeData.setPressCorrect(true);
                saveTimeData.setPhysicalKey(getPhysicKey(saveTimeData));
                correctKeys.add(saveTimeData);
            }
        } else {
            saveTimeData.setPressCorrect(false);
            correctKeys.remove(saveTimeData);
        }
    }

    public void caCorrectKey(int physicKey, boolean press) {
        if (physicKeys == null) physicKeys = new ArrayList<>();
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
        for (SaveTimeData saveTimeData : correctKeys) {
            if (saveTimeData.getPhysicalKey() == physicKey) {
                if (press) {
                    saveTimeData.setHasRecord(true);
                    correctNodes++;
                } else {
                }
            }

        }
    }

    public List<SaveTimeData> getCurrentKeys() {
        return correctKeys;

    }

    private Integer getPhysicKey(SaveTimeData saveTimeData) {
        return 31;
    }

    public int caLastScores() {
        return (int) ((float) correctNodes / (float) totalNodes * 100);
    }
}
