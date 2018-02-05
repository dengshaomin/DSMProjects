package com.yizu.intelligentpiano.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.dialog.TimeDialog;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.widget.MyVideoView;

import jp.kshoji.driver.midi.device.MidiInputDevice;

/**
 * 视频播放
 */
public class VideoActivity extends BaseActivity {
    private final static String TAG = "VideoActivity";
    private MyBroadcastReceiver receiver;
    private MyVideoView mVideoView;
    private ImageView play;
//    private RelativeLayout mTime;
    protected static final int PROGRESS = 0;
    private boolean isPlay = true;
    private SeekBar seekBar;
    private String mPath = "";
    private AlertDialog mDialog;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS:
                    //获得当前进度
                    if (isPlay) {
                        int progress = mVideoView.getCurrentPosition();
                        //获得当前视频的总时长
                        int duration = mVideoView.getDuration();
                        seekBar.setProgress(progress);
                        seekBar.setMax(duration);
//                        MyLogUtils.e(TAG, "当前进度" + progress);
                        handler.sendEmptyMessageDelayed(PROGRESS, 200);
                    }

                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void initView() {
        mVideoView = findViewById(R.id.vedioview);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.saekBar);
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
//        String urls = SDCardUtils.getIsHave(Constents.VIDEO_URL.concat("/" + title + "_" + auther + ".mp4"));
//            if (urls.equals("")) {
//                downLoadFile(xml, title + "_" + auther + ".mp4", Constents.VIDEO_URL);
//            } else {
//                playView(urls);
//            }
    }


    /**
     * 播放视频
     *
     * @param urls
     */
    private void playView(String urls) {
        mPath = urls;
        //系统自带的进度条
//        MediaController controller = new MediaController(this);
//        controller.setVisibility(View.GONE);
        //设置视频控制器
//        mVideoView.setMediaController(controller);
        //开始播放
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放完成回调
        mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                MyToast.ShowLong("播放失败");
                finish();
                return false;
            }
        });
        mVideoView.setVideoPath(mPath);
        //播放
        mVideoView.start();
    }

    private DownloadUtils utils;
    /**
     * 下载xml文件
     *
     * @param fileUrl  下载文件的网络路径
     * @param fileName 保存的文件名
     * @param saveUrl  保存的文件夹
     */
    private void downLoadFile(String fileUrl, final String fileName, final String saveUrl) {
//        "http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"
        if (fileUrl == null) return;
        if (utils==null)utils = new DownloadUtils(this);
        utils.downloadFile(fileUrl,
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

                    @Override
                    public void apk() {

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
                    new TimeDialog(VideoActivity.this);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mDialog != null && mDialog.isShowing()) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    if (play.isSelected()) {
                        isPlay = false;
                        play.setSelected(false);
                        //暂停
                        mVideoView.pause();
                    } else {
                        isPlay = true;
                        play.setSelected(true);
//                        if (mVideoView.isPlaying()) {
//                            mVideoView.resume();
//                        } else {
//                        }
                        mVideoView.start();
                        handler.sendEmptyMessage(PROGRESS);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完毕
//            finish();
            isPlay = false;
            play.setSelected(false);
            mVideoView.pause();
            mVideoView.stopPlayback();//停止播放,释放资源
            mVideoView.setVideoPath(mPath);//重新设置资源
//            mVideoView.start();
            seekBar.setProgress(0);
            showDialogs();
            MyLogUtils.e(TAG,"播放完成");
        }
    }
    //显示播放完成
    private void showDialogs() {
        if (mDialog!=null&&mDialog.isShowing())return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("播放完毕，是否退出？");
        builder.setNegativeButton("重新播放", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isPlay = true;
                play.setSelected(true);
                mVideoView.start();
            }
        });
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        mDialog = builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlay = false;
        if (mVideoView != null) {
            mVideoView.suspend();  //将VideoView所占用的资源释放掉
        }
        if (receiver != null) unregisterReceiver(receiver);
        if (utils != null) {
            utils.onDrestry();
        }
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            isPlay = true;
            play.setSelected(true);
            handler.sendEmptyMessage(PROGRESS);
        }
    }
}
