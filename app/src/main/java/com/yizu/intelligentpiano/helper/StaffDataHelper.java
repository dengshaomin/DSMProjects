package com.yizu.intelligentpiano.helper;

import com.yizu.intelligentpiano.bean.PullData;
import com.yizu.intelligentpiano.bean.SaveTimeData;
import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.Measure;
import com.yizu.intelligentpiano.bean.xml.MeasureBase;
import com.yizu.intelligentpiano.bean.xml.Notes;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.utils.MyLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/12/5.
 * Email: chenhuixueba@163.com
 * 处理五线谱数据的帮助类
 */

public class StaffDataHelper {
    private static String TAG = "StaffDataHelper";
    private static StaffDataHelper instence;
    //五线谱的属性信息
    private Attributess mAttributess;
    //第一条线的数据
    private List<Measure> mFristStaffData=new ArrayList<>();
    //第二条线的数据
    private List<Measure> mSecondStaffData =new ArrayList<>();
    //保存绘制瀑布流的数据
    private List<PullData> pullData=new ArrayList<>();
    //保存整个谱子升降音的数组
    private String[] fifth;
    //开头是升还是降银
    private boolean isUpfifth = false;
    //默认每分钟88拍
    private int DEFAULT_TIME_NUM = 80;
    //true：二条五线谱
    private boolean isTowStaff = false;
    private List<Integer> mBackUPData =new ArrayList<>();
    private int divisions = 0;
    private int beats = 0;
    //每小节多少Duration
    private int measureDurationNum = 0;
    //默认每个druction的长度20像素
    private int mSpeedLenth = 20;
    //每个duration多少毫秒
    private float mSpeedTime = 0;

    private StaffDataHelper() {
    }

    public static synchronized StaffDataHelper getInstence() {
        if (instence == null) {
            instence = new StaffDataHelper();
        }
        return instence;
    }

    /**
     * 解析五线谱数据
     * @param staffData
     * @param iFinish
     */
    public void AnalyticStaffData(List<Measure> staffData, IFinish iFinish) {
        MyLogUtils.e(TAG,"开始转化五线谱数据");

        mFristStaffData.clear();
        mSecondStaffData.clear();
        pullData.clear();
        mBackUPData.clear();
        mAttributess = null;

        //该音符之前的总duration
        int fristTimeDuration = 0;
        int secondTimeDuration = 0;
        for (int j = 0; j < staffData.size(); j++) {
            List<MeasureBase> list = new ArrayList<>();
            List<MeasureBase> list1 = new ArrayList<>();
            List<SaveTimeData> fristTime = new ArrayList<>();
            List<SaveTimeData> secondTime = new ArrayList<>();

            boolean isBackUp = false;
            int measureSize = staffData.get(j).getMeasure().size();
            for (int k = 0; k < measureSize; k++) {
                if (mAttributess == null && staffData.get(j).getMeasure().get(k).getAttributes() != null) {
                    //五线谱信息
                    mAttributess = staffData.get(j).getMeasure().get(k).getAttributes();
                    //处理整条五线谱的升降音
                    initFifthData(mAttributess.getKey().getFifths());
                    if (mAttributess.getStaves() != null && mAttributess.getStaves().equals("2")) {
                        isTowStaff = true;
                    } else {
                        isTowStaff = false;
                    }
                } else if (staffData.get(j).getMeasure().get(k).getSound() != null) {
//                    建议拍数
                    DEFAULT_TIME_NUM = Integer.valueOf(staffData.get(j).getMeasure().get(k).getSound());
                } else {
                    if (staffData.get(j).getMeasure().get(k).getBackup() != null) {
                        isBackUp = true;
//                        往前移动backup个距离
                        secondTimeDuration = fristTimeDuration - Integer.valueOf(staffData.get(j).getMeasure().get(k).getBackup().getDuration());
                        //保存backup数据
                        mBackUPData.add(Integer.valueOf(staffData.get(j).getMeasure().get(k).getBackup().getDuration()));
                    } else {
                        if (!isBackUp) {
                            list.add(staffData.get(j).getMeasure().get(k));
                            Notes notes = staffData.get(j).getMeasure().get(k).getNotes();
                            if (notes != null) {
                                if (notes.getRest()) {
                                    fristTime.add(new SaveTimeData(fristTimeDuration, Integer.valueOf(notes.getDuration()), true));
                                    fristTimeDuration += Integer.valueOf(notes.getDuration());
                                } else if (notes.getPitch() != null) {
                                    //重新设置瀑布流数据
                                    fristTimeDuration = setPullView(fristTime, fristTimeDuration, notes);
                                }
                            }
                        } else {
                            //五线谱第二条线
                            list1.add(staffData.get(j).getMeasure().get(k));
                            //瀑布流第二线数据
                            Notes notes = staffData.get(j).getMeasure().get(k).getNotes();
                            if (notes != null) {
                                if (notes.getRest()) {
                                    secondTime.add(new SaveTimeData(secondTimeDuration, Integer.valueOf(notes.getDuration()), true));
                                    secondTimeDuration += Integer.valueOf(notes.getDuration());
                                } else if (notes.getPitch() != null) {
                                    secondTimeDuration = setPullView(secondTime, secondTimeDuration, notes);
                                }
                            }

                        }
                    }
                }
            }
            mFristStaffData.add(new Measure(list));
            mSecondStaffData.add(new Measure(list1));
            pullData.add(new PullData(fristTime, secondTime));
        }
        divisions = Integer.valueOf(mAttributess.getDivisions());
        beats = Integer.valueOf(mAttributess.getTime().getBeats());
        //每个duraction的时间
        mSpeedTime = 60 * 1000 / (DEFAULT_TIME_NUM * divisions);

        //每一小节的duraction数量
        measureDurationNum = divisions * beats;

//        每小节长度为320
        mSpeedLenth = 320 / measureDurationNum;
        if (mSpeedLenth < 2) mSpeedLenth = 2;//每个druction不小于1
        if (mAttributess==null)return;
        if (mFristStaffData.size()==0) return;
        if (mSecondStaffData.size()==0) return;
        if (pullData.size()==0)return;
        MyLogUtils.e(TAG,"转化五线谱数据成功");
        iFinish.success();
    }


    /**
     * 从新设置瀑布流数据
     *
     * @param list
     * @param duration
     * @param notes
     */
    private int setPullView(List<SaveTimeData> list, int duration, Notes notes) {
        //键组
        int octave = Integer.valueOf(notes.getPitch().getOctave());
        //音域
        String step = notes.getPitch().getStep();
        String saveStep = step;
        String alter = notes.getPitch().getAlter();//本音符是否生姜
        int black = 0;

        //处理单个音符的升降
        if (alter != null) {
            if (octave == 0) {
                switch (step) {
                    case "A":
                        switch (alter) {
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "B";
                                break;
                        }
                        break;
                    case "B":
                        switch (alter) {
                            case "-2":
                                step = "A";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "C";
                                octave++;
                                black = 0;
                                break;
                        }
                        break;
                }
            } else {
                switch (step) {
                    case "C":
                        switch (alter) {
                            case "-2":
                                step = "B";
                                octave--;
                                black = 0;
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "D";
                                break;
                        }
                        break;
                    case "D":
                        switch (alter) {
                            case "-2":
                                step = "C";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "E";
                                break;
                        }
                        break;
                    case "E":
                        switch (alter) {
                            case "-2":
                                step = "D";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "F";
                                black = 0;
                                break;
                        }
                        break;
                    case "F":
                        switch (alter) {
                            case "-2":
                                step = "E";
                                black = -0;
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "G";
                                break;
                        }
                        break;
                    case "G":
                        switch (alter) {
                            case "-2":
                                step = "F";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "A";
                                break;
                        }
                        break;
                    case "A":
                        switch (alter) {
                            case "-2":
                                step = "G";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "B";
                                break;
                        }
                        break;
                    case "B":
                        switch (alter) {
                            case "-2":
                                step = "A";
                                break;
                            case "-1":
                                black = -1;
                                break;
                            case "1":
                                black = 1;
                                break;
                            case "2":
                                step = "C";
                                octave++;
                                black = 0;
                                break;
                        }
                        break;
                }
            }
        }

        //整条五线谱升降调
        if (fifth != null) {
            for (int i = 0; i < fifth.length; i++) {
                if (fifth[i] == saveStep && !alter.equals("0")) {
                    if (octave == 0) {
                        switch (step) {
                            case "A":
                                if (isUpfifth) {
                                    if (black == 1) {
                                        step = "B";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                }
                                break;
                            case "B":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        octave = 1;
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                        }
                    } else {
                        switch (step) {
                            case "C":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "D";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        octave = 0;
                                        step = "B";
                                    } else {
                                        black--;
                                    }

                                }
                                break;
                            case "D":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "E";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "E":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "F";
                                        black = 0;
                                    } else {
                                        black++;
                                    }

                                } else {
                                    if (black == -1) {
                                        step = "D";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "F":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "G";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "E";
                                        black = 0;
                                    } else {
                                        black--;
                                    }

                                }
                                break;
                            case "G":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "F";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "A":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        step = "B";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "G";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                            case "B":
                                if (isUpfifth) {
                                    //升调
                                    if (black == 1) {
                                        octave++;
                                        step = "C";
                                        black = 0;
                                    } else {
                                        black++;
                                    }
                                } else {
                                    if (black == -1) {
                                        step = "A";
                                        black = 0;
                                    } else {
                                        black--;
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
        boolean istia = false;
        if (notes.getTie() != null && notes.getTie().size() != 0) {
            if (notes.getTie().get(0).equals("start")) istia = true;
        }
        SaveTimeData time = new SaveTimeData(duration, Integer.valueOf(notes.getDuration()), octave, step, black, istia);

        if (!notes.getChord()) {
            duration += Integer.valueOf(notes.getDuration());
        } else {
            time.setmAddDuration(list.get(list.size() - 1).getmAddDuration());
        }
        list.add(time);
        return duration;
    }

    /**
     * 初始化整个五线谱的升降音
     *
     * @param fifths
     */
    private void initFifthData(String fifths) {
        if (fifths == null) return;
        switch (fifths) {
            case "1":
                isUpfifth = true;
                fifth = new String[]{"F"};
                break;
            case "2":
                isUpfifth = true;
                fifth = new String[]{"F", "C"};
                break;
            case "3":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G"};
                break;
            case "4":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D"};
                break;
            case "5":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A"};
                break;
            case "6":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A", "E"};
                break;
            case "7":
                isUpfifth = true;
                fifth = new String[]{"F", "C", "G", "D", "A", "E", "B"};
                break;
            case "-1":
                fifth = new String[]{"B"};
                break;
            case "-2":
                fifth = new String[]{"B", "E"};
                break;
            case "-3":
                fifth = new String[]{"B", "E", "A"};
                break;
            case "-4":
                fifth = new String[]{"B", "E", "A", "D"};
                break;
            case "-5":
                fifth = new String[]{"B", "E", "A", "D", "G"};
                break;
            case "-6":
                fifth = new String[]{"B", "E", "A", "D", "G", "C"};
                break;
            case "-7":
                fifth = new String[]{"B", "E", "A", "D", "G", "C", "F"};
                break;
        }
    }

    public Attributess getmAttributess() {
        return mAttributess;
    }

    public List<Measure> getmFristStaffData() {
        return mFristStaffData;
    }

    public List<Measure> getmSecondStaffData() {
        return mSecondStaffData;
    }

    public List<PullData> getPullData() {
        return pullData;
    }

    public String[] getFifth() {
        return fifth;
    }

    public boolean isUpfifth() {
        return isUpfifth;
    }

    public int getDEFAULT_TIME_NUM() {
        return DEFAULT_TIME_NUM;
    }

    public boolean isTowStaff() {
        return isTowStaff;
    }

    public List<Integer> getmBackUPData() {
        return mBackUPData;
    }

    public int getDivisions() {
        return divisions;
    }

    public int getBeats() {
        return beats;
    }

    public int getMeasureDurationNum() {
        return measureDurationNum;
    }

    public int getmSpeedLenth() {
        return mSpeedLenth;
    }

    public float getmSpeedTime() {
        return mSpeedTime;
    }
}
