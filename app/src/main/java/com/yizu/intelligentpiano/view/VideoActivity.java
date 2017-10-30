package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.utils.DownloadUtils;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.ShowTimeDialog;

/**
 * 视频播放
 */
public class VideoActivity extends BaseActivity {
    private final static String TAG = "VideoActivity";
    private MyBroadcastReceiver receiver;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void initView() {
        mVideoView = findViewById(R.id.vedioview);
    }

    @Override
    protected void setData() {
        setRegisterReceiver();
//        if (getIntent() != null) {
//            String title = getIntent().getStringExtra("title");
//            String auther = getIntent().getStringExtra("auther");
//            String xml = getIntent().getStringExtra("xml");
//        }

        String title = "小小心里话";
        String auther = "卓依婷";
        String xml = "http://piano.sinotransfer.com/Uploads/Download/2017-09-27/59cb16c92f1c2.mp4";

        String urls = SDCardUtils.getIsHave(Constents.VIDEO_URL.concat("/" + title + "_" + auther + ".mp4"));
        if (urls.equals("")) {
            downLoadFile(xml, title + "_" + auther + ".mp4", Constents.VIDEO_URL);
        } else {
            playView(urls);
        }
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
//        //网络视频
//        String videoUrl2 = "http://sodimg.fzhuaqing.com/public/attachment/201710/20/13/59e98d80504fc.mp4";
//        Uri uri = Uri.parse(videoUrl2);
//        //设置视频路径
//        mVideoView.setVideoURI(uri);

        mVideoView.setVideoPath(urls);

        //开始播放视频
        mVideoView.start();
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
                        playView(urls);
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
                    new ShowTimeDialog(VideoActivity.this, VideoActivity.this).showTimeView(findViewById(R.id.main_piano));
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:

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
