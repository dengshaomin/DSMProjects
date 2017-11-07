package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.constens.ScoreHelper;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.XmlPrareUtils;
import com.yizu.intelligentpiano.widget.PianoKeyView;
import com.yizu.intelligentpiano.widget.PrgoressView;
import com.yizu.intelligentpiano.widget.PullView;
import com.yizu.intelligentpiano.widget.StaffView;

import java.util.HashMap;
import java.util.Map;

import jp.kshoji.driver.midi.activity.AbstractSingleMidiActivity;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;

/**
 * 钢琴演奏
 */
public class PianoActivity extends AbstractSingleMidiActivity implements View.OnClickListener {
    private static final String TAG = "PianoActivity";
    private MyBroadcastReceiver receiver;
    private PianoKeyView mPianoKeyView;
    private StaffView mStaffView;
    private PullView mPullView;
    private PrgoressView mProgessView;
    private RelativeLayout mTime;
    private RelativeLayout mScore;
    private TextView score_score, score_again, score_exit, score_songname;
    private ImageView score_img;
    //播放
    private ImageView mPlay;
    //快进
    private ImageView mSpeed;
    //慢放
    private ImageView mRewind;
    private TextView realyTimeScore;

    //是否显示瀑布流
    private boolean isShowPull = true;
    //是否处理按键
    private boolean KeyIsOk = false;
    //实时得分
    private int realyScore = 0;

    private ImageView mIcon;
    private TextView mNickName;
    private TextView mSongName;

    /**
     * 前面数据
     */
    //昵称
    private String nickName;
    //icon
    private String icon;
    //歌曲名字
    private String music_title;
    //歌曲作者
    private String music_auther;
    private String music_id;
    //歌曲类型
    private String music_type;
    private String music_xml;
    private SelectActivity selectActivity;

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
                mPianoKeyView.painoKeyCanclePress(msg.what);
//                打分
                ScoreHelper.getInstance().caCorrectKey(msg.what, false);
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);
        setRegisterReceiver();
        initView();
        setData();
        setListener();
    }

    /**
     * 下载xml文件
     *
     * @param fileUrl  下载文件的网络路径
     * @param fileName 保存的文件名
     * @param saveUrl  保存的文件夹
     */
    private void downLoadFile(String fileUrl, final String fileName, final String saveUrl) {
        new DownloadUtils(this).downloadFile(fileUrl,
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
                });
    }

    /**
     * 获取xml数据
     *
     * @param urls
     */
    private void getXmlData(String urls) {
        MyLogUtils.e(TAG, "xmlUrls：" + urls);
        XmlPrareUtils utils = new XmlPrareUtils(this);
        XmlBean bean = utils.getXmlBean(urls);
        if (bean == null) {
            MyLogUtils.e(TAG, "解析失败");
            return;
        }
        mStaffView.setStaffData(bean.getList(), new IFinish() {
            @Override
            public void success() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mProgessView.setPrgoressData(mStaffView);
                    }
                });
                //更新PullView的数据
                mPullView.setPullData(mStaffView, mPianoKeyView, mProgessView);
            }
        });

    }

    private void setListener() {
        mPlay.setOnClickListener(this);
        mSpeed.setOnClickListener(this);
        mRewind.setOnClickListener(this);
        score_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScore.setVisibility(View.GONE);
            }
        });
        score_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScore.setVisibility(View.GONE);
                PianoActivity.this.finish();
            }
        });
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
                showResultView(realyScore);
                //上传打分
//                addMusicHistory();
            }
        });
    }

    public void setData() {
        getSongsData();

//        mPullView.isShow(true);
//        type = 3;
//        title = "伤不起";
//        auther = "邹浩";
//        String xml = "http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml";
//        initData(type, title, auther, xml);
    }

    /**
     * 获取歌曲信息
     */
    private void getSongsData() {
        selectActivity = SelectActivity.selectActivity;
        if (selectActivity == null) return;
        selectActivity.getData(new IGetSelectData() {
            @Override
            public void data(String nickname, String icon, boolean isShowPull,
                             String music_type, String music_title, String music_auther,
                             String music_xml, String music_id) {
                PianoActivity.this.nickName = nickname;
                PianoActivity.this.icon = icon;
                PianoActivity.this.isShowPull = isShowPull;

                PianoActivity.this.music_type = music_type.replace("|", ".");
                PianoActivity.this.music_title = music_title;
                PianoActivity.this.music_auther = music_auther;
                PianoActivity.this.music_xml = music_xml;
                PianoActivity.this.music_id = music_id;
            }
        });
        MyLogUtils.e(TAG, "nickName：" + nickName);
        MyLogUtils.e(TAG, "icon：" + icon);
        MyLogUtils.e(TAG, "title：" + music_title);
        MyLogUtils.e(TAG, "auther：" + music_auther);
        MyLogUtils.e(TAG, "xml：" + music_xml);
        MyLogUtils.e(TAG, "type：" + music_type);
        MyLogUtils.e(TAG, "isShowPull：" + isShowPull);
        //设置是否显示瀑布流
        mPullView.isShow(isShowPull);
        mNickName.setText(nickName);
        Glide.with(PianoActivity.this).load(icon).into(mIcon);
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
        String urls = SDCardUtils.getIsHave(Constents.XML.concat("/" + music_type + "_" + music_title + "_" + music_auther + ".xml"));
        if (urls.equals("")) {
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

    private void initView() {
        realyTimeScore = findViewById(R.id.realyTimeScore);
        mPianoKeyView = findViewById(R.id.piano_key);
        mStaffView = findViewById(R.id.staffview);
        mPullView = findViewById(R.id.pullview);
        mPlay = findViewById(R.id.play);
        mSpeed = findViewById(R.id.speed);
        mRewind = findViewById(R.id.rewind);
        mProgessView = findViewById(R.id.prgoressView);
        mTime = findViewById(R.id.time);
        mScore = findViewById(R.id.score_view);
        score_score = findViewById(R.id.score_score);
        score_again = findViewById(R.id.score_again);
        score_exit = findViewById(R.id.score_exit);
        score_img = findViewById(R.id.score_img);
        score_songname = findViewById(R.id.score_songname);
        mIcon = findViewById(R.id.user_icon);
        mNickName = findViewById(R.id.user_name);
        mSongName = findViewById(R.id.user_song);
    }


    //    打分上传
    private void addMusicHistory() {
        Map<String, String> map = new HashMap<>();
        map.put("music_id", music_id);
        map.put("music_title", music_title);
        map.put("device_id", PreManger.instance().getMacId());
        map.put("user_id", Constents.user_id);
        map.put("score", realyScore + "");
        map.put("auther", music_auther);
        OkHttpUtils.postMap(HttpUrls.addMusicHistory, map, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                MyLogUtils.e(TAG, "上传成功");
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
        score_score.setText(scores + "分");
        score_songname.setText(music_title + "—" + music_auther);
    }

    //midi链接
    @Override
    public void onDeviceAttached(@NonNull UsbDevice usbDevice) {
        MyLogUtils.e(TAG, "onDeviceAttached");
        //此方法已经废弃
    }

    @Override
    public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
        MyLogUtils.e(TAG, "onMidiInputDeviceAttached");
        //钢琴键盘可以将midi传入到手机
        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已链接");
        KeyIsOk = true;
    }

    @Override
    public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
        //手机可以将midi传入到钢琴键盘
        MyLogUtils.e(TAG, "onMidiOutputDeviceAttached");
        KeyIsOk = true;
    }

    //midi断开
    @Override
    public void onDeviceDetached(@NonNull UsbDevice usbDevice) {
        MyLogUtils.e(TAG, "onDeviceDetached");
        //此方法已经废弃
    }

    @Override
    public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
        MyLogUtils.e(TAG, "onMidiInputDeviceDetached");
        //usb断开，钢琴键盘不可以将midi传入到手机
        MyToast.ShowLong("设备ID：" + midiInputDevice.getUsbDevice().getDeviceId() + ",已断开");
        KeyIsOk = false;
    }

    @Override
    public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
        MyLogUtils.e(TAG, "onMidiOutputDeviceDetached");
        //usb断开，手机不可以将midi传入到钢琴键盘
        KeyIsOk = false;
    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiMiscellaneousFunctionCodes");
    }

    @Override
    public void onMidiCableEvents(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiCableEvents");
    }

    @Override
    public void onMidiSystemCommonMessage(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {
        MyLogUtils.e(TAG, "onMidiSystemCommonMessage");
    }

    @Override
    public void onMidiSystemExclusive(@NonNull MidiInputDevice midiInputDevice, int i, byte[] bytes) {
        MyLogUtils.e(TAG, "onMidiSystemExclusive");
    }

    /**
     * 钢琴手指抬起
     *
     * @param midiInputDevice
     * @param i
     * @param i1
     * @param i2
     * @param i3
     */
    @Override
    public void onMidiNoteOff(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiNoteOff" + "NoteOn cable: " + i + ",  channel: " + i1 + ", note: " + i2 + ", velocity: " + i3);
        canclePrassHandler.sendEmptyMessage(i2);
    }

    /**
     * 钢琴手指按下
     *
     * @param midiInputDevice 输入的设备
     * @param i
     * @param i1
     * @param i2              键数代表键的位置
     * @param i3
     */
    @Override
    public void onMidiNoteOn(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiNoteOn" + "NoteOn cable: " + i + ",  channel: " + i1 + ", note: " + i2 + ", velocity: " + i3);
        prassHandler.sendEmptyMessage(i2);

    }

    @Override
    public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiPolyphonicAftertouch");
    }

    @Override
    public void onMidiControlChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2, int i3) {
        MyLogUtils.e(TAG, "onMidiControlChange");
    }

    @Override
    public void onMidiProgramChange(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
        MyLogUtils.e(TAG, "onMidiProgramChange");
    }

    @Override
    public void onMidiChannelAftertouch(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
        MyLogUtils.e(TAG, "onMidiChannelAftertouch");
    }

    @Override
    public void onMidiPitchWheel(@NonNull MidiInputDevice midiInputDevice, int i, int i1, int i2) {
        MyLogUtils.e(TAG, "onMidiPitchWheel");
    }

    @Override
    public void onMidiSingleByte(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
        MyLogUtils.e(TAG, "onMidiSingleByte");
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
        MyLogUtils.e(TAG, "onMidiTimeCodeQuarterFrame");
    }

    @Override
    public void onMidiSongSelect(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
        MyLogUtils.e(TAG, "onMidiSongSelect");
    }

    @Override
    public void onMidiSongPositionPointer(@NonNull MidiInputDevice midiInputDevice, int i, int i1) {
        MyLogUtils.e(TAG, "onMidiSongPositionPointer");
    }

    @Override
    public void onMidiTuneRequest(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiTuneRequest");
    }

    @Override
    public void onMidiTimingClock(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiTimingClock");
    }

    @Override
    public void onMidiStart(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiStart");
    }

    @Override
    public void onMidiContinue(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiContinue");
    }

    @Override
    public void onMidiStop(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiStop");
    }

    @Override
    public void onMidiActiveSensing(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiActiveSensing");
    }

    @Override
    public void onMidiReset(@NonNull MidiInputDevice midiInputDevice, int i) {
        MyLogUtils.e(TAG, "onMidiReset");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPullView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPullView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (mPlay.isSelected()) {
                    mPlay.setSelected(false);
                } else {
                    mPlay.setSelected(true);
                }

                mPullView.play();
                break;
            case R.id.speed:
                //快放
                mPullView.accelerate();
                break;
            case R.id.rewind:
//                慢放
                mPullView.deceleration();
                break;
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT_FINISH:
                    //activity直接退出
                    PianoActivity.this.finish();
                    break;
                case Constents.NOTIME_5:
                    //剩余5分钟
                    mTime.setVisibility(View.VISIBLE);
                    break;
                case Constents.MUSIC:
                    //推送音乐
                    if (mPlay.isSelected()) {
                        mPlay.setSelected(false);
                        mPullView.play();
                    }
                    if (mPullView != null) {
                        mPullView.resetPullView();
                    }
                    if (mStaffView != null) {
                        mStaffView.resetPullView();
                    }
                    getSongsData();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyIsOk) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    //左
                    return true;
                case KeyEvent.KEYCODE_ALT_RIGHT:
                    //右
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mTime.getVisibility() == View.VISIBLE) {
                        mTime.setVisibility(View.GONE);
                    } else {
                        //确定
                        if (mPlay.isSelected()) {
//                暂停
                            mPlay.setSelected(false);
                            mPullView.play();
//                    mStaffView.stopPlay();
//                            mProgessView.stopPlay();
                        } else {
//                播放
                            mPlay.setSelected(true);
                            mPullView.play();
//                    mStaffView.startPlay();
//                            mProgessView.startPlay();
                        }
                    }

                    return true;
            }
        }
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
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
