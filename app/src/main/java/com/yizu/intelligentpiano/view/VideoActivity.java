package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.SDCardUtils;

import jp.kshoji.driver.midi.device.MidiInputDevice;

/**
 * 视频播放
 */
public class VideoActivity extends BaseActivity {
    private final static String TAG = "VideoActivity";
    private MyBroadcastReceiver receiver;
    private VideoView mVideoView;
    private boolean isOk = false;
    private ImageView play;
    private RelativeLayout mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void initView() {
        mVideoView = findViewById(R.id.vedioview);
        play = findViewById(R.id.play);
        mTime = findViewById(R.id.time);
    }

    @Override
    protected void setData() {
        setRegisterReceiver();
        if (getIntent() != null) {
            String title = getIntent().getStringExtra("title");
            String auther = getIntent().getStringExtra("auther");
            String xml = getIntent().getStringExtra("xml");
            MyLogUtils.e(TAG, "xml:  " + xml);
            String urls = SDCardUtils.getIsHave(Constents.VIDEO_URL.concat("/" + title + "_" + auther + ".mp4"));
            if (urls.equals("")) {
                downLoadFile(xml, title + "_" + auther + ".mp4", Constents.VIDEO_URL);
            } else {
                playView(urls);
            }
        }
//        String title = "小小心里话";
//        String auther = "卓依婷";
//        String xml = "http://piano.sinotransfer.com/Uploads/Download/2017-09-27/59cb16c92f1c2.mp4";
    }


    /**
     * 播放视频
     *
     * @param urls
     */
    private void playView(String urls) {
        //设置视频控制器
        mVideoView.setMediaController(new MediaController(this));
        //播放完成回调
        mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        mVideoView.setVideoPath(urls);
        isOk = true;
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
                fileName, DownloadUtils.FileType.VIDEO, saveUrl, new IDwonLoader() {
                    @Override
                    public void video() {
                        MyLogUtils.e(TAG, "下载成功");
                        String urls = SDCardUtils.getExternalStorageDirectory().concat(saveUrl + "/" + fileName);
                        playView(urls);
                    }

                    @Override
                    public void Xml() {

                    }
                });
    }


    @Override
    protected void setLinster() {

    }

    /**
     * 注册动态广播
     */
    private void setRegisterReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter(Constents.ACTION);
        registerReceiver(receiver, iFilter);
    }

    /**
     * 广播
     */
    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT_FINISH:
                    //activity直接退出
                    VideoActivity.this.finish();
                    break;
                case Constents.NOTIME_5:
                    //剩余5分钟
                    mTime.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mTime.getVisibility() == View.VISIBLE) {
                    mTime.setVisibility(View.GONE);
                } else {
                    if (play.isSelected()) {
                        play.setSelected(false);
                        //暂停
                        mVideoView.pause();
                    } else {
                        play.setSelected(true);
                        //播放
                        mVideoView.start();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_ENTER:
                if (mTime.getVisibility() == View.VISIBLE) {
                    mTime.setVisibility(View.GONE);
                } else {
                    if (play.isSelected()) {
                        play.setSelected(false);
                        //暂停
                        mVideoView.pause();
                    } else {
                        play.setSelected(true);
                        //播放
                        mVideoView.start();
                    }
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完毕
            finish();
        }
    }
}
