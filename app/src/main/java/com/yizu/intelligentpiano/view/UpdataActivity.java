package com.yizu.intelligentpiano.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IDwonLoader;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.utils.DownloadUtils;

import jp.kshoji.driver.midi.device.MidiInputDevice;

/**
 * 系统更新界面
 */

public class UpdataActivity extends BaseActivity {
    private DownloadUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void setData() {
        if (!getIntent().getStringExtra("url").equals("")) {
            if (utils==null)utils = new DownloadUtils(this);
            //链接地址，保存包名
            utils.downloadFile(getIntent().getStringExtra("url"), "智能钢琴.apk", DownloadUtils.FileType.APK, Constents.APK_URL, new IDwonLoader() {
                @Override
                public void video() {

                }

                @Override
                public void Xml() {

                }

                @Override
                public void apk() {
                }
            });
        } else {
            finish();
        }
    }


    @Override
    protected void setLinster() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (utils != null) {
            utils.onDrestry();
        }
    }
}
