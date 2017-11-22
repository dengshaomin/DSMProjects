package com.yizu.intelligentpiano.utils;

import android.content.Context;
import android.util.Xml;

import com.yizu.intelligentpiano.bean.xml.Attributess;
import com.yizu.intelligentpiano.bean.xml.AttributessKey;
import com.yizu.intelligentpiano.bean.xml.AttributessTime;
import com.yizu.intelligentpiano.bean.xml.Backup;
import com.yizu.intelligentpiano.bean.xml.Barline;
import com.yizu.intelligentpiano.bean.xml.Beam;
import com.yizu.intelligentpiano.bean.xml.Clef;
import com.yizu.intelligentpiano.bean.xml.Measure;
import com.yizu.intelligentpiano.bean.xml.MeasureBase;
import com.yizu.intelligentpiano.bean.xml.Notes;
import com.yizu.intelligentpiano.bean.xml.Pitch;
import com.yizu.intelligentpiano.bean.xml.XmlBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/8.
 * All Rights Reserved by YiZu
 * http://www.cnblogs.com/amosli/p/3769118.html//这个网址很溜
 */

public class XmlPrareUtils {
    private final static String TAG = "XmlPrareUtils";

    private XmlBean bean;
    private Context context;

    private List<Measure> measureList;
    private Measure measure;
    private List<MeasureBase> measureBaseList;
    private MeasureBase measureBase;
    private Attributess attributess;
    private AttributessKey attributessKey;
    private AttributessTime attributessTime;
    private List<Clef> clefList;
    private Clef clef;
    private Notes notes;
    private Pitch pitch;
    private Barline barline;
    private Backup backup;

//    private String sdCardUrl;

    private List<Beam> beams;

    private List<String> tie;
    private List<String> slur;


    public XmlPrareUtils(Context context) {
        this.context = context;
//        sdCardUrl = SDCardUtils.getExternalStorageDirectory() + "/PianoApp/xml/";
    }

    public XmlBean getXmlBean(String fileUrl) {
        //assets资源管理器
//        AssetManager manager = context.getAssets();
        if (fileUrl.equals("")) {
            return null;
        }
        File file = new File(fileUrl);
        if (!file.isFile()) {
            MyToast.ShowLong("没有找到该xml文件");
            return null;
        }
        try {

            FileInputStream stream = new FileInputStream(file);
            if (stream == null) {
                MyToast.ShowLong("xml文件解析失败");
                return null;
            }
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(stream, "utf-8");
            //获取pull解析器对应事件类型
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {//读到文档结束位置
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        bean = new XmlBean();
                        MyLogUtils.e(TAG, "读到文档开始位置，开始解析");
                        break;
                    case XmlPullParser.START_TAG:
//                        MyLogUtils.e(TAG, "读到元素开始的位置");
                        setParser(xmlPullParser, eventType);
                        break;
                    case XmlPullParser.END_TAG:
                        endParser(xmlPullParser);
//                        MyLogUtils.e(TAG, "读到元素结束的位置");
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 每个标签解析结束
     *
     * @param xmlPullParser
     */
    private void endParser(XmlPullParser xmlPullParser) {
        switch (xmlPullParser.getName()) {
            case "part":
                bean.setList(measureList);
                break;
            case "key":
                attributess.setKey(attributessKey);
                break;
            case "time":
                attributess.setTime(attributessTime);
                break;
            case "clef":
                clefList.add(clef);
                break;
            case "pitch":
                notes.setPitch(pitch);
                break;
            case "attributes":
                if (clefList != null) {
                    attributess.setClefList(clefList);
                    measureBase.setAttributes(attributess);
                    clefList = null;
                }
                measureBaseList.add(measureBase);
                break;
            case "backup":
                measureBaseList.add(measureBase);
                break;
            case "note":
                if (beams != null) {
                    notes.setBeam(beams);
                    beams = null;
                }
                if (slur != null) {
                    notes.setSlur(slur);
                    slur = null;
                }
                measureBase.setNotes(notes);
                measureBaseList.add(measureBase);
                break;
            case "barline":
                measureBase.setBarline(barline);
                measureBaseList.add(measureBase);
                break;
            case "measure":
                if (measureBaseList != null) {
                    measure.setMeasure(measureBaseList);
                    measureBaseList = null;
                }
                if (measureList == null) {
                    measureList = new ArrayList<>();
                }
                measureList.add(measure);
                measure = null;
                break;
            case "notations":
                if (tie != null) {
                    notes.setTie(tie);
                    tie = null;
                }
                break;
        }
    }

    /**
     * 解析MuicssXml数据
     *
     * @param xmlPullParser
     * @param eventType
     */
    private void setParser(XmlPullParser xmlPullParser, int eventType) {
        try {
            switch (xmlPullParser.getName()) {
                case "measure":
                    measure = new Measure();
                    measure.setNumber(xmlPullParser.getAttributeValue(null, "number"));
                    measureBaseList = new ArrayList<>();
                    break;
                case "attributes":
                    measureBase = new MeasureBase();
                    attributess = new Attributess();
                    break;
                case "sound":
                    String sound = xmlPullParser.getAttributeValue(null, "tempo");
                    measureBase.setSound(sound);
                    measureBaseList.add(measureBase);
                    break;
                case "divisions":
                    attributess.setDivisions(xmlPullParser.nextText());
                    break;
                case "key":
                    attributessKey = new AttributessKey();
                    break;
                case "fifths":
                    attributessKey.setFifths(xmlPullParser.nextText());
                    break;
                case "mode":
                    attributessKey.setMode(xmlPullParser.nextText());
                    break;
                case "time":
                    attributessTime = new AttributessTime();
                    break;
                case "beats":
                    attributessTime.setBeats(xmlPullParser.nextText());
                    break;
                case "beat-type":
                    attributessTime.setBeat_type(xmlPullParser.nextText());
                    break;
                case "staves":
                    attributess.setStaves(xmlPullParser.nextText());
                    break;
                case "clef":
                    if (clefList == null) {
                        clefList = new ArrayList<>();
                    }
                    clef = new Clef();
                    clef.setNumber(xmlPullParser.getAttributeValue(null, "number"));
                    break;
                case "sign":
                    clef.setSign(xmlPullParser.nextText());
                    break;
                case "line":
                    clef.setLine(xmlPullParser.nextText());
                    break;
                case "note":
                    measureBase = new MeasureBase();
                    notes = new Notes();
                    break;
                case "chord":
                    notes.setChords(true);
                    break;
                case "pitch":
                    pitch = new Pitch();
                    break;
                case "step":
                    pitch.setStep(xmlPullParser.nextText());
                    break;
                case "alter":
                    pitch.setAlter(xmlPullParser.nextText());
                    break;
                case "octave":
                    pitch.setOctave(xmlPullParser.nextText());
                    break;
                case "voice":
                    notes.setVoice(xmlPullParser.nextText());
                    break;
                case "type":
                    notes.setType(xmlPullParser.nextText());
                    break;
                case "dot":
                    notes.setDots(true);
                    break;
                case "rest":
                    notes.setRest(true);
                    break;
                case "tie":
                    if (tie == null) {
                        tie = new ArrayList<>();
                    }
                    tie.add(xmlPullParser.getAttributeValue(null, "type"));
                    break;
                case "stem":
                    String stem = xmlPullParser.nextText();
                    notes.setStems(stem);
                    break;
                case "staff":
                    notes.setStaff(xmlPullParser.nextText());
                    break;
                case "backup":
                    measureBase = new MeasureBase();
                    backup = new Backup();
                    break;
                case "slur":
                    if (slur == null) {
                        slur = new ArrayList<>();
                    }
                    slur.add(xmlPullParser.getAttributeValue(null, "type"));
                    break;
                case "beam":
                    String num = xmlPullParser.getAttributeValue(null, "number");
                    String beam = xmlPullParser.nextText();
                    if (beams == null) {
                        beams = new ArrayList<>();
                    }
                    beams.add(new Beam(num, beam));
                    break;
                case "duration":
                    if (backup != null) {
                        backup.setDuration(xmlPullParser.nextText());
                        measureBase.setBackup(backup);
                        backup = null;
                    } else {
                        notes.setDuration(xmlPullParser.nextText());
                    }
                    break;
                case "barline":
                    measureBase = new MeasureBase();
                    barline = new Barline();
                    barline.setLocation(xmlPullParser.getAttributeValue(null, "location"));
                    break;
                case "bar-style":
                    barline.setBar_style(xmlPullParser.nextText());
                    break;
                default:
                    //每次都执行这一句
                    eventType = xmlPullParser.next();
                    break;
            }

            //3.解析defaults

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
