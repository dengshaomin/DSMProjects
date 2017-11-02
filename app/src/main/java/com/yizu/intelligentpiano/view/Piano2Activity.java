package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.xml.XmlBean;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.constens.IPlayState;
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
import com.yizu.intelligentpiano.widget.PullSurfaceView;
import com.yizu.intelligentpiano.widget.ScoreResultView;
import com.yizu.intelligentpiano.widget.StaffView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.kshoji.driver.midi.activity.AbstractSingleMidiActivity;
import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;

/**
 * 钢琴演奏
 */
public class Piano2Activity extends AbstractSingleMidiActivity implements View.OnClickListener, ScoreHelper.ScoreCallBack {
    private static final String TAG = "PianoActivity";
    //    private PopupWindow popupWindow;
    private MyBroadcastReceiver receiver;
    private PianoKeyView mPianoKeyView;
    private StaffView mStaffView;
    private PullSurfaceView mPullView;
    private PrgoressView mProgessView;
    private RelativeLayout mTime;
    private RelativeLayout mScore;
    private TextView scoreView, score_again, score_exit;
    private ImageView score_img;
    //播放
    private ImageView mPlay;
    //快进
    private ImageView mSpeed;
    //慢放
    private ImageView mRewind;
    private TextView realyTimeScore;

    private int type = 0;
    //是否显示瀑布流
    private boolean isShowPull = true;
    //是否处理按键
    private boolean KeyIsOk = false;
    //实时得分
    private int realyScore = 0;

    private Handler prassHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what > 20 && msg.what < 109) {
                mPianoKeyView.painoKeyPress(msg.what);
                ScoreHelper.getInstance().caCorrectKey(msg.what, true);
                timer.schedule(new PressTimerTask(), 3000);
            }
            return true;
        }
    });
    private Handler canclePrassHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what > 20 && msg.what < 109) {
                mPianoKeyView.painoKeyCanclePress(msg.what);
                ScoreHelper.getInstance().caCorrectKey(msg.what, false);
                timer1.schedule(new UpTimerTask(), 3000);
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
        setContentView(R.layout.activity_piano2);

        setRegisterReceiver();
        initView();
        setData();
        setListener();
        sendTestPhysicKeys();
    }

    Timer timer;
    Timer timer1;

    private void sendTestPhysicKeys() {
        timer = new Timer();
        timer.schedule(new PressTimerTask(), 2000);
        timer1 = new Timer();
        timer1.schedule(new UpTimerTask(), 3000);
    }

    @Override
    public void callBack(int score) {
        realyScore = score;
        realyTimeScore.setText("当前得分：" + score + "分");
    }

    private class PressTimerTask extends TimerTask {
        @Override
        public void run() {
            prassHandler.sendEmptyMessage(52);
        }
    }

    private class UpTimerTask extends TimerTask {
        @Override
        public void run() {
            canclePrassHandler.sendEmptyMessage(52);
        }
    }

    /**
     * 下载xml文件
     *
     * @param fileUrl  下载文件的网络路径
     * @param fileName 保存的文件名
     * @param saveUrl  保存的文件夹
     */
    private void downLoadFile(String fileUrl, final String fileName, final String saveUrl) {
//        "http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"
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
                mProgessView.setPrgoressData(mStaffView);
                //更新PullView的数据
                mPullView.setPullData(mStaffView, mPianoKeyView,mProgessView);
            }
        });

    }

    private void setListener() {
        mPlay.setOnClickListener(this);
    }

    private void setData() {
//        if (getIntent() != null) {
//            String title = getIntent().getStringExtra("title");
//            MyLogUtils.e(TAG, "title：" + title);
//            String auther = getIntent().getStringExtra("auther");
//            MyLogUtils.e(TAG, "auther：" + auther);
//            String xml = getIntent().getStringExtra("xml");
//            MyLogUtils.e(TAG, "xml：" + xml);
//            type = getIntent().getIntExtra("type", 0);
//            MyLogUtils.e(TAG, "type：" + type);
//            isShowPull = getIntent().getBooleanExtra("isShowPull", true);
//            MyLogUtils.e(TAG, "isShowPull：" + isShowPull);
//            //设置是否显示瀑布流
//            mPullView.isShow(isShowPull);
//            initData(type, title, auther, xml);
//        }
        type = 3;
        String title = "伤不起";
        String auther = "邹浩";
        String xml = "http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml";
        initData(type, title, auther, xml);
    }

    /**
     * 判断xml是否需要下载
     *
     * @param type
     * @param title
     * @param auther
     * @param xml
     */
    private void initData(int type, String title, String auther, String xml) {
        String url = "";
        switch (type) {
            case 1:
                url = Constents.XML_CHILDREN;
                break;
            case 2:
                url = Constents.XML_SATINE;
                break;
            case 3:
                url = Constents.XML_NOSTALGIC;
                break;
            case 4:
                url = Constents.XML_POPULAR;
                break;
            case 5:
                url = Constents.XML_GAME;
                break;
            case 6:
                url = Constents.XML_SENTIMENTAL;
                break;
        }
        if (url.equals("")) {
            return;
        }
        String urls = SDCardUtils.getIsHave(url.concat("/" + title + "_" + auther + ".xml"));
        if (urls.equals("")) {
            downLoadFile(xml, title + "_" + auther + ".xml", url);
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
        mPianoKeyView = (PianoKeyView) findViewById(R.id.piano_key);
        mStaffView = (StaffView) findViewById(R.id.staffview);
        mPullView = (PullSurfaceView) findViewById(R.id.pullview);
        mPlay = (ImageView) findViewById(R.id.play);
        mSpeed = (ImageView) findViewById(R.id.speed);
        mRewind = (ImageView) findViewById(R.id.rewind);
        mProgessView = (PrgoressView) findViewById(R.id.prgoressView);
        mTime = findViewById(R.id.time);
        mScore = findViewById(R.id.score_view);
        scoreView = findViewById(R.id.score_score);
        score_again = findViewById(R.id.score_again);
        score_exit = findViewById(R.id.score_exit);
        score_img = findViewById(R.id.score_img);
        score_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScore.setVisibility(View.GONE);
                startPlay();
            }
        });
        score_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScore.setVisibility(View.GONE);
            }
        });
//        mPullView.setiPlayState(new IPlayState() {
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void end() {
////                int scores = ScoreHelper.getInstance().caLastScores();
//                showResultView(realyScore);
//            }
//        });

//        mProgessView.setiPlayState(new IPlayState() {
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void end() {
////                int scores = ScoreHelper.getInstance().caLastScores();
////                showResultView(scores);
//            }
//        });
        ScoreHelper.getInstance().setCallback(this);
    }


    //    打分上传
    private void addMusicHistory() {
        Map<String, String> map = new HashMap<>();
        map.put("music_id", "23");
        map.put("music_title", "我在想你");
        map.put("device_id", PreManger.instance().getMacId());
        map.put("user_id", Constents.user_id);
        map.put("score", "88");
        map.put("auther", "刘海");
        OkHttpUtils.postMap(HttpUrls.addMusicHistory, map, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                MyLogUtils.e(TAG, "上传成功");
            }
        });
    }

    private ScoreResultView scoreResultView;

    /**
     * 显示成绩
     */
    private void showResultView(int scores) {
        mScore.setVisibility(View.VISIBLE);
        boolean isGood = scores > 90;
        score_img.setBackgroundResource(isGood ? R.mipmap.good : R.mipmap.bad);
        scoreView.setBackgroundResource(isGood ? R.mipmap.score_good : R.mipmap.score_bad);
        scoreView.setText(scores + "");
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

    private boolean pullViewState = false;

    private void startPlay() {
        mPullView.play();
//        if (pullViewState) {
//            mProgessView.stopPlay();
//        } else {
//            ScoreHelper.getInstance().reset();
//            mProgessView.startPlay();
//        }
        pullViewState = !pullViewState;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                startPlay();
                break;
            case R.id.speed:
                //快放
                break;
            case R.id.record:
//                慢放
                break;
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT_FINISH:
                    //activity直接退出
                    Piano2Activity.this.finish();
                    break;
                case Constents.NOTIME_5:
                    //剩余5分钟
                    mTime.setVisibility(View.VISIBLE);
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

}
