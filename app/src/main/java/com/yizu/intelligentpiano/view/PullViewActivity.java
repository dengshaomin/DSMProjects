package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.xml.XmlBean;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.constens.IGetSelectData;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.dialog.TimeDialog;
import com.yizu.intelligentpiano.helper.ScoreHelper;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.XmlPrareUtils;
import com.yizu.intelligentpiano.widget.PianoKeyView;
import com.yizu.intelligentpiano.widget.PullView;

import java.util.HashMap;
import java.util.Map;

import jp.kshoji.driver.midi.device.MidiInputDevice;

public class PullViewActivity extends BasePullActivity implements View.OnClickListener {

    private static final String TAG = "PullViewActivity";
    private MyBroadcastReceiver receiver;
    private PianoKeyView mPianoKeyView;
    private PullView mPullView;
    private RelativeLayout mScore;
    private TextView score_score, score_again, score_exit, score_songname;
    private ImageView score_img;
    //播放
    private ImageView mPlay;
    //快进
    private ImageView mSpeed;
    //慢放
    private ImageView mRewind;
    //当前得分
    private TextView realyTimeScore;

    //是否处理按键，默认连接键盘
//    private boolean KeyIsOk = true;
    //实时得分
    private int realyScore = 0;

    private ImageView mIcon;
    private TextView mNickName;
    private TextView mSongName;
    private TextView mTimesSpeed;

    /**
     * 前面数据
     */
    //昵称
    private String nickName = "";
    //icon
    private String icon = "";
    //歌曲名字
    private String music_title = "";
    //歌曲作者
    private String music_auther = "";
    private String music_id = "";
    //歌曲类型
    private String music_type = "";
    private String music_xml = "";
    private SelectActivity selectActivity;
    private DownloadUtils utils;

    private MyThred myThred;

    private Handler prassHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what > 20 && msg.what < 109) {
                mPianoKeyView.painoKeyPress(msg.what);
                ScoreHelper.getInstance().caCorrectKey(msg.what, true);
            }
            return true;
        }
    });
    private Handler canclePrassHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what > 20 && msg.what < 109) {
                ScoreHelper.getInstance().caCorrectKey(msg.what, false);
                mPianoKeyView.painoKeyCanclePress(msg.what);
            }
            return true;
        }
    });

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TimeDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_view);
    }

    /**
     * 下载xml文件
     *
     * @param fileUrl  下载文件的网络路径
     * @param fileName 保存的文件名
     * @param saveUrl  保存的文件夹
     */
    private void downLoadFile(String fileUrl, final String fileName, final String saveUrl) {
        if (utils==null)utils = new DownloadUtils(this);
        utils.downloadFile(fileUrl,
                fileName, DownloadUtils.FileType.XML, saveUrl, new IDwonLoader() {
                    @Override
                    public void video() {

                    }

                    @Override
                    public void Xml() {
                        MyLogUtils.e(TAG, "下载成功");
                        String urls = SDCardUtils.getExternalStorageDirectory().concat(saveUrl + "/" + fileName);
                        getXmlData(urls);
                    }

                    @Override
                    public void apk() {

                    }
                });
    }

    /**
     * 获取xml数据
     *
     * @param urls
     */
    private void getXmlData(String urls) {
        MyLogUtils.e(TAG, "xmlUrls：" + urls);
        XmlPrareUtils utils = new XmlPrareUtils();
        XmlBean bean = utils.getXmlBean(urls);
        if (bean == null || bean.getList() == null) {
//            MyToast.ShowLong("解析失败");
            return;
        }
        StaffDataHelper.getInstence().AnalyticStaffData(bean.getList(), new IFinish() {
            @Override
            public void success() {
                //显示瀑布流,更新PullView的数据
                mPullView.setPullData(mPianoKeyView, new IPlay() {
                    @Override
                    public void ReadyFinish() {
                        MyLogUtils.e(TAG, "瀑布流初始化完成");
                        mPlay.setSelected(true);
                        mTimesSpeed.setText(mPullView.getmReta());
                        mPullView.play(true);
                    }
                });
            }
        });

    }

    @Override
    protected void setLinster() {
        mPlay.setOnClickListener(this);
        mSpeed.setOnClickListener(this);
        mRewind.setOnClickListener(this);
        //实时打分
        ScoreHelper.getInstance().setCallback(new ScoreHelper.ScoreCallBack() {
            @Override
            public void callBack(int score) {
                realyScore = score;
                realyTimeScore.setText("当前得分：" + realyScore + "分");
            }
        });
        //播放结束
        mPullView.setiPlayEnd(new IPlayEnd() {
            @Override
            public void end() {
                mPlay.setSelected(false);
                //上传打分
                addMusicHistory();
            }
        });
    }

    @Override
    protected void setData() {
        setRegisterReceiver();
        getSongsData();
//        test();
    }

//    private void test() {
//        music_type = "2017-12-16_15-12-01";
//        music_title = "梦中的婚礼";
//        music_auther = "lalagu";
//        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-11/5a06f8178327f.xml";
//
////        music_type = "2017-12-16 17-45-59";
////        music_title = "天空之城";
////        music_auther = "lalago";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-13/5a091ce51406f.xml";
//
////        music_type = "2017-12-16 17-45-08";
////        music_title = "别问我是谁";
////        music_auther = "lalagu";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-11/5a06d4ad5c7e8.xml";
//
////        music_type = "2017-12-16 15-12-01";
////        music_title = "月亮代表我的心";
////        music_auther = "lalagu";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-11/5a06f8178327f.xml";
//        if (myThred != null) {
//            myThred.interrupt();
//            myThred = null;
//        }
//        myThred = new MyThred();
//        myThred.start();
//    }


    /**
     * 获取歌曲信息
     */
    private void getSongsData() {
        selectActivity = SelectActivity.selectActivity;
        if (selectActivity == null) return;
        selectActivity.getData(new IGetSelectData() {
            @Override
            public void data(String nickname, String icon,
                             String music_updatatime, String music_title, String music_auther,
                             String music_xml, String music_id) {
                PullViewActivity.this.nickName = nickname;
                PullViewActivity.this.icon = icon;

                PullViewActivity.this.music_type = music_updatatime.replaceAll(":", "-");
                PullViewActivity.this.music_title = music_title;
                PullViewActivity.this.music_auther = music_auther;
                PullViewActivity.this.music_xml = music_xml;
                PullViewActivity.this.music_id = music_id;
            }
        });
        MyLogUtils.e(TAG, "nickName：" + nickName);
        MyLogUtils.e(TAG, "icon：" + icon);
        MyLogUtils.e(TAG, "title：" + music_title);
        MyLogUtils.e(TAG, "auther：" + music_auther);
        MyLogUtils.e(TAG, "xml：" + music_xml);
        MyLogUtils.e(TAG, "type：" + music_type);

        if (nickName != null && !nickName.equals("")) mNickName.setText(nickName);
        if (icon != null && !icon.equals("")) Glide.with(this).load(icon).into(mIcon);
        mSongName.setText(music_title + "—" + music_auther);
        if (myThred != null) {
            myThred.interrupt();
            myThred = null;
        }
        myThred = new MyThred();
        myThred.start();
    }

    /**
     * 判断xml是否需要下载
     */
    private void initData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                realyScore = 0;
                realyTimeScore.setText("当前得分：" + realyScore + "分");
            }
        });
        String urls = SDCardUtils.getIsHave(Constents.XML.concat("/" + music_type + "_" + music_title + "_" + music_auther + ".xml"));
        if (urls.equals("")) {
            MyLogUtils.e(TAG, "开始下载");
            downLoadFile(music_xml, music_type + "_" + music_title + "_" + music_auther + ".xml", Constents.XML);
        } else {
            getXmlData(urls);
        }
    }

    /**
     * 注册动态广播
     */
    private void setRegisterReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter(Constents.ACTION);
        registerReceiver(receiver, iFilter);
    }

    @Override
    protected void initView() {
        realyTimeScore = findViewById(R.id.realyTimeScore);
        mPianoKeyView = findViewById(R.id.piano_key);
        mPullView = findViewById(R.id.pullview);
        mPlay = findViewById(R.id.play);
        mSpeed = findViewById(R.id.speed);
        mRewind = findViewById(R.id.rewind);
        mScore = findViewById(R.id.score_view);
        score_score = findViewById(R.id.score_score);
        score_again = findViewById(R.id.score_again);
        score_exit = findViewById(R.id.score_exit);
        score_img = findViewById(R.id.score_img);
        score_songname = findViewById(R.id.score_songname);
        mIcon = findViewById(R.id.user_icon);
        mNickName = findViewById(R.id.user_name);
        mSongName = findViewById(R.id.user_song);
        mTimesSpeed = findViewById(R.id.times_speed);
    }


    //    打分上传
    private void addMusicHistory() {
        if (realyScore == 0) return;
        if (music_id.equals("")) return;
        if (music_title.equals("")) return;
        if (music_auther.equals("")) return;
//        if (nickName.equals("")) return;
        Map<String, String> map = new HashMap<>();
        map.put("music_id", music_id);
        map.put("music_title", music_title);
        map.put("device_id", PreManger.instance().getMacId());
        map.put("user_id", Constents.user_id);
        map.put("score", realyScore + "");
        map.put("auther", music_auther);
        OkHttpUtils.getInstance().postMap(HttpUrls.addMusicHistory, map, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                showResultView(realyScore);
                selectActivity.isUpdata = true;
            }
        });
    }

    /**
     * 显示成绩
     */
    private void showResultView(int scores) {
        mScore.setVisibility(View.VISIBLE);
        boolean isGood = scores > 79;
        score_img.setBackgroundResource(isGood ? R.mipmap.good : R.mipmap.bad);
        score_score.setBackgroundResource(isGood ? R.mipmap.score_good : R.mipmap.score_bad);
        score_again.setSelected(true);
        score_exit.setSelected(false);
        score_score.setText(scores + "分");
        score_songname.setText(music_title + "—" + music_auther);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (utils != null) {
            utils.onDrestry();
        }
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (mPlay.isSelected()) {
                    mPlay.setSelected(false);
                    mPullView.play(false);
                } else {
                    mPlay.setSelected(true);
                    mPullView.play(true);
                }
                break;
            case R.id.speed:
                //快放
                mTimesSpeed.setText(mPullView.accelerate());
                break;
            case R.id.rewind:
//                慢放
                mTimesSpeed.setText(mPullView.deceleration());
                break;
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT_FINISH:
                    //activity直接退出
                    PullViewActivity.this.finish();
                    break;
                case Constents.NOTIME_5:
                    //剩余5分钟
                    dialog=new TimeDialog(PullViewActivity.this);
                    dialog.show();
                    mPullView.play(false);
                    break;
                case Constents.MUSIC:
                    //推送音乐
                    if (mScore.getVisibility() == View.VISIBLE) {
                        mScore.setVisibility(View.GONE);
                    } else if (null!=dialog&&dialog.isShowing()) {
                        dialog.dissmiss();
                    }
                    if (mPullView != null) {
                        mPullView.resetPullView();
                    }
                    if (mPlay.isSelected()) {
                        mPlay.setSelected(false);
                        mPullView.play(false);
                    }
                    getSongsData();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (KeyIsOk) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //左
                if (mScore.getVisibility() == View.VISIBLE) {
                    score_again.setSelected(true);
                    score_exit.setSelected(false);
                    MyLogUtils.e(TAG, "再来一次");
                } else {
                    //慢放
                    mTimesSpeed.setText(mPullView.deceleration());
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //右
                if (mScore.getVisibility() == View.VISIBLE) {
                    score_again.setSelected(false);
                    score_exit.setSelected(true);
                    MyLogUtils.e(TAG, "退出");
                } else  {
                    //快放
                    mTimesSpeed.setText(mPullView.accelerate());
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mScore.getVisibility() == View.VISIBLE) {
                    if (score_again.isSelected()) {
                        //再来一次
                        mScore.setVisibility(View.GONE);
                        mPlay.setSelected(true);
                        mPullView.play(true);
                    } else {
                        //退出
                        mScore.setVisibility(View.GONE);
                        PullViewActivity.this.finish();
                    }
                } else {
                    //播放，暂停
                    if (mPlay.isSelected()) {
                        mPlay.setSelected(false);
                        mPullView.play(false);
                    } else {
                        mPlay.setSelected(true);
                        mPullView.play(true);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (mScore.getVisibility() == View.VISIBLE) {
                    mScore.setVisibility(View.GONE);
                    return true;
                }
                return super.onKeyDown(keyCode, event);
        }
//        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 开启线程
     */
    class MyThred extends Thread {
        @Override
        public void run() {
            super.run();
            initData();
        }
    }

    @Override
    protected void onMidiCanInput(MidiInputDevice midiInputDevice) {
        MyLogUtils.e(TAG, "onMidiInputDeviceAttached");
        //钢琴键盘可以将midi传入到手机
        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已链接");
//        KeyIsOk = true;
    }

    @Override
    protected void onMidiNoInput(MidiInputDevice midiInputDevice) {
        MyLogUtils.e(TAG, "onMidiInputDeviceDetached");
        //usb断开，钢琴键盘不可以将midi传入到手机
        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已断开");
//        KeyIsOk = false;
//        keyCancleHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onNoteOn(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        prassHandler.sendEmptyMessage(i2);
    }

    @Override
    protected void onNoteOff(MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        canclePrassHandler.sendEmptyMessage(i2);
    }
}
