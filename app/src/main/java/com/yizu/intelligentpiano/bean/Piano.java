package com.yizu.intelligentpiano.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;

import com.yizu.intelligentpiano.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxiaozhu on 2017/10/9.
 * All Rights Reserved by YiZu
 * 钢琴实体
 */

public class Piano {

    private Context mContext;
    //黑白键高度和宽度
    private int mBlackKeyWidth;
    private int mBlackKeyHeight;
    private int mWhiteKeyWidth;
    private int mWhiteKeyHeight;

    //黑白键的组数
    private final static int BLACK_PIANO_KEY_GROUPS = 8;
    private final static int WHITE_PIANO_KEY_GROUPS = 9;
    private final static int BLACK_AND_WHITE_KEY_GROUPS_NUMS = 12;

    //黑白键集合
    private ArrayList<PianoKey[]> blackPianoKeys = new ArrayList<>(BLACK_PIANO_KEY_GROUPS);
    private ArrayList<PianoKey[]> whitePianoKeys = new ArrayList<>(WHITE_PIANO_KEY_GROUPS);
    private Map<Integer, PianoKey> map = new HashMap<>();
    private List<PianoKey> keyList = new ArrayList<>();
    //宽高缩放比
    private float scaleWidth;
    private float scaleHeight;

    public Piano(Context context, int blackKeyHeight,
                 int blackKeyWidth, int whiteKeyHeight,
                 int whiteKeyWidth, float scaleHeight, float scaleWidth) {
        mContext = context;
        mBlackKeyHeight = blackKeyHeight;
        mBlackKeyWidth = blackKeyWidth;
        mWhiteKeyHeight = whiteKeyHeight;
        mWhiteKeyWidth = whiteKeyWidth;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
        initBlackKey();
        initWhiteKey();
    }

    /**
     * 初始化所有白键
     */
    private void initWhiteKey() {
        //初始化白键
        for (int i = 0; i < WHITE_PIANO_KEY_GROUPS; i++) {
            switch (i) {
                case 0:
                    PianoKey keys0[] = new PianoKey[2];
                    for (int j = 0; j < keys0.length; j++) {
                        keys0[j] = new PianoKey();
                        keys0[j].setType(PianoKeyType.WHITE);
                        keys0[j].setKeyDrawable(
                                new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.white_piano_key),
                                        Gravity.NO_GRAVITY, scaleWidth, scaleHeight).getDrawable()
                        );
                        setWhiteKeyDrawableBounds(i, j, keys0[j].getKeyDrawable());
                        setWhiteMap(i, j, keys0[j]);
                    }
                    whitePianoKeys.add(keys0);
                    break;
                case 8:
                    PianoKey keys1[] = new PianoKey[1];
                    for (int j = 0; j < keys1.length; j++) {
                        keys1[j] = new PianoKey();
                        keys1[j].setType(PianoKeyType.WHITE);
                        keys1[j].setKeyDrawable(
                                new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.white_piano_key),
                                        Gravity.NO_GRAVITY, scaleWidth, scaleHeight).getDrawable()
                        );
                        setWhiteKeyDrawableBounds(i, j, keys1[j].getKeyDrawable());
                        setWhiteMap(i, j, keys1[j]);
                    }
                    whitePianoKeys.add(keys1);
                    break;
                default:
                    PianoKey keys2[] = new PianoKey[7];
                    for (int j = 0; j < keys2.length; j++) {
                        keys2[j] = new PianoKey();
                        //固定属性
                        keys2[j].setType(PianoKeyType.WHITE);
                        keys2[j].setKeyDrawable(
                                new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.white_piano_key),
                                        Gravity.NO_GRAVITY, scaleWidth, scaleHeight).getDrawable()
                        );
                        setWhiteKeyDrawableBounds(i, j, keys2[j].getKeyDrawable());
                        setWhiteMap(i, j, keys2[j]);
                    }
                    whitePianoKeys.add(keys2);
                    break;
            }
        }
    }

    /**
     * 初始化黑键
     */
    private void initBlackKey() {
        for (int i = 0; i < BLACK_PIANO_KEY_GROUPS; i++) {
            if (i == 0) {
                PianoKey[] keys0 = new PianoKey[1];
                for (int j = 0; j < keys0.length; j++) {
                    keys0[j] = new PianoKey();
                    keys0[j].setType(PianoKeyType.BLACK);
                    keys0[j].setKeyDrawable(
                            new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.black_piano_key),
                                    Gravity.NO_GRAVITY, scaleWidth, scaleHeight).getDrawable()
                    );
                    setBlackKeyDrawableBounds(i, j, keys0[j].getKeyDrawable());
                    setBlackMap(i, j, keys0[j]);
                }
                blackPianoKeys.add(keys0);
            } else {
                PianoKey[] keys1 = new PianoKey[5];
                for (int j = 0; j < keys1.length; j++) {
                    keys1[j] = new PianoKey();
                    keys1[j].setType(PianoKeyType.BLACK);
                    keys1[j].setKeyDrawable(
                            new ScaleDrawable(ContextCompat.getDrawable(mContext, R.drawable.black_piano_key),
                                    Gravity.NO_GRAVITY, scaleWidth, scaleHeight).getDrawable()
                    );
                    setBlackKeyDrawableBounds(i, j, keys1[j].getKeyDrawable());
                    setBlackMap(i, j, keys1[j]);
                }
                blackPianoKeys.add(keys1);
            }
        }
    }

    public enum PianoKeyType {
        BLACK, WHITE
    }

    /**
     * 设置黑色键图案的位置
     *
     * @param group           组数，从0开始
     * @param positionOfGroup 组内的位置
     * @param drawable        要设置的Drawale对象
     */
    private void setBlackKeyDrawableBounds(int group, int positionOfGroup, Drawable drawable) {
        int whiteOffset = 0;
        int blackOffset = 0;
        if (group == 0) {
            whiteOffset = 5;
        }
        if (positionOfGroup == 2 || positionOfGroup == 3 || positionOfGroup == 4) {
            blackOffset = 1;
        }
        drawable.setBounds(
                (7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * mWhiteKeyWidth - mBlackKeyWidth / 2 - 30,
                0,
                (7 * group - 4 + whiteOffset + blackOffset + positionOfGroup) * mWhiteKeyWidth + mBlackKeyWidth / 2 - 30,
                mBlackKeyHeight
        );
    }

    /**
     * 设置白色键图案的位置
     *
     * @param group           组数，从0开始
     * @param positionOfGroup 在本组中的位置
     * @param drawable        要设置的Drawale对象
     */
    private void setWhiteKeyDrawableBounds(int group, int positionOfGroup, Drawable drawable) {
        int offset = 0;
        if (group == 0) {
            offset = 5;
        }
        drawable.setBounds(
                (7 * group - 5 + offset + positionOfGroup) * mWhiteKeyWidth - 30,
                0,
                (7 * group - 4 + offset + positionOfGroup) * mWhiteKeyWidth - 1 - 30,
                mWhiteKeyHeight - 1
        );
    }

    private void setBlackMap(int group, int positionOfGroup, PianoKey pianoKey) {
        int num = 22;
        if (group != 0) {
            num = 25 + positionOfGroup * 2 + (group - 1) * BLACK_AND_WHITE_KEY_GROUPS_NUMS;
            if (positionOfGroup > 1) {
                num++;
            }
        }
        map.put(num, pianoKey);
    }

    private void setWhiteMap(int group, int positionOfGroup, PianoKey pianoKey) {
        int num = 21;
        if (group == 0) {
            if (positionOfGroup == 0) {
                num += positionOfGroup;
            } else {
                num = num + 2;
            }
        } else {
            num = 24 + positionOfGroup * 2 + (group - 1) * BLACK_AND_WHITE_KEY_GROUPS_NUMS;
            if (positionOfGroup > 2) {
                num--;
            }
        }
        map.put(num, pianoKey);
    }


    public ArrayList<PianoKey[]> getBlackPianoKeys() {
        return blackPianoKeys;
    }

    public ArrayList<PianoKey[]> getWhitePianoKeys() {
        return whitePianoKeys;
    }

    /**
     * 获取所有键的顺序组合
     *
     * @return
     */
    public List<PianoKey> getKeyList() {
        for (int i = 21; i < map.size() + 21; i++) {
            keyList.add(map.get(i));
        }
        return keyList;
    }
}
