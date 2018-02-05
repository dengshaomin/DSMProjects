package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.constens.IGetSelectData;
import com.yizu.intelligentpiano.constens.IPlay;
import com.yizu.intelligentpiano.constens.IPlayEnd;
import com.yizu.intelligentpiano.dialog.TimeDialog;
import com.yizu.intelligentpiano.helper.StaffDataHelper;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.XmlPrareUtils;
import com.yizu.intelligentpiano.widget.ProgresView;
import com.yizu.intelligentpiano.widget.StaffView;


/**
 * 钢琴演奏
 */
public class PianoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PianoActivity";
    private MyBroadcastReceiver receiver;
    private StaffView mStaffView;
    //播放
    private ImageView mPlay;
    //快进
    private ImageView mSpeed;
    //慢放
    private ImageView mRewind;

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
    //歌曲类型
    private String music_type = "";
    private String music_xml = "";

    private MyThred myThred;
    private ProgresView progress;
    private DownloadUtils utils;
    private TimeDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);
    }

    /**
     * 下载xml文件
     *
     * @param fileUrl  下载文件的网络路径
     * @param fileName 保存的文件名
     * @param saveUrl  保存的文件夹
     */
    private void downLoadFile(String fileUrl, final String fileName, final String saveUrl) {
        if (utils == null) utils = new DownloadUtils(this);
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
            MyLogUtils.e(TAG, "五线谱初始化失败");
            return;
        }
        StaffDataHelper.getInstence().AnalyticStaffData(bean.getList(), new IFinish() {
            @Override
            public void success() {
                //显示五线谱
                mStaffView.setStaffData(progress, new IPlay() {
                    @Override
                    public void ReadyFinish() {
                        MyLogUtils.e(TAG, "五线谱初始化完成");
                        mPlay.setSelected(true);
                        mTimesSpeed.setText(progress.getmReta());
                        progress.play(true);
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
        progress.setPlayEnd(new IPlayEnd() {
            @Override
            public void end() {
                mPlay.setSelected(false);
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
////        music_type = "2017-12-22_22-47-38";
////        music_title = "爱不爱我";
////        music_auther = "lalogo";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-12-22/5a3d1a9522f66.xml";
////        music_type = "2017-12-20_15-10-15";
////        music_title = "红河谷";
////        music_auther = "lalogo";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-12-20/5a3a0cd5d99d0.xml";
////
////        music_type = "2017-12-20_14-53-27";
////        music_title = "国际歌";
////        music_auther = "lalogo";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-12-20/5a3a08e5ee8e4.xml";
//
////        music_type = "2017-12-16_15-12-01";
////        music_title = "梦中的婚礼";
////        music_auther = "lalagu";
////        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-11/5a06f8178327f.xml";
//
//        music_type = "2017-12-16 17-45-59";
//        music_title = "天空之城";
//        music_auther = "lalago";
//        music_xml = "http://piano.sinotransfer.com/Uploads/Download/2017-11-13/5a091ce51406f.xml";
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
//
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
        MyLogUtils.e(TAG, "获取歌曲信息");
        SelectActivity selectActivity = SelectActivity.selectActivity;
        if (selectActivity == null) return;
        selectActivity.getData(new IGetSelectData() {
            @Override
            public void data(String nickname, String icon, String music_updatatime,
                             String music_title, String music_auther, String music_xml,
                             String music_id) {
                PianoActivity.this.nickName = nickname;
                PianoActivity.this.icon = icon;
                PianoActivity.this.music_type = music_updatatime.replaceAll(":", "-");
                PianoActivity.this.music_title = music_title;
                PianoActivity.this.music_auther = music_auther;
                PianoActivity.this.music_xml = music_xml;
            }
        });
        if (nickName != null && !nickName.equals("")) mNickName.setText(nickName);
        if (icon != null && !icon.equals("")) Glide.with(PianoActivity.this).load(icon).into(mIcon);
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

    @Override
    protected void initView() {
        mStaffView = findViewById(R.id.staffview);
        mPlay = findViewById(R.id.play);
        mSpeed = findViewById(R.id.speed);
        mRewind = findViewById(R.id.rewind);
        mIcon = findViewById(R.id.user_icon);
        mNickName = findViewById(R.id.user_name);
        mSongName = findViewById(R.id.user_song);
        mTimesSpeed = findViewById(R.id.times_speed);
        progress = findViewById(R.id.progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.onDrestry();
        mStaffView.onDrestry();
        if (utils != null) {
            utils.onDrestry();
        }
        if (myThred != null) {
            myThred.interrupt();
            myThred = null;
        }
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (mPlay.isSelected()) {
                    mPlay.setSelected(false);
                    progress.play(false);

                } else {
                    mPlay.setSelected(true);
                    progress.play(true);
                }
                break;
            case R.id.speed:
                //快放
                mTimesSpeed.setText(progress.accelerate());
                break;
            case R.id.rewind:
//                慢放
                mTimesSpeed.setText(progress.deceleration());
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
                    dialog=new TimeDialog(PianoActivity.this);
                    dialog.show();
                    progress.play(false);
                    break;
                case Constents.MUSIC:
                    //推送音乐
                    progress.isPush();
                    if (null!=dialog&&dialog.isShowing()) {
                        dialog.dissmiss();
                    }
                    if (mPlay.isSelected()) {
                        mPlay.setSelected(false);
                        progress.play(false);
                    }
                    getSongsData();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //左
                if (null==dialog||!dialog.isShowing()) {
                    //慢放
                    mTimesSpeed.setText(progress.deceleration());
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //右
                if (null==dialog||!dialog.isShowing()) {
                    //快放
                    mTimesSpeed.setText(progress.accelerate());
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //播放，暂停
                if (mPlay.isSelected()) {
                    mPlay.setSelected(false);
                    progress.play(false);
                } else {
                    mPlay.setSelected(true);
                    progress.play(true);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                return super.onKeyDown(keyCode, event);
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
        }
    }
}
