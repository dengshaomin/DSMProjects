package com.yizu.intelligentpiano.view;

import android.os.Bundle;
import android.widget.TextView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.utils.DownloadUtils;

/**
 * 系统更新界面
 */

public class UpdataActivity extends BaseActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata);
    }

    @Override
    protected void initView() {
//        textView = (TextView) findViewById(R.id.updata);
    }

    @Override
    protected void setData() {
        if (!getIntent().getStringExtra("url").equals("")) {
            DownloadUtils utils = new DownloadUtils(this);
            //链接地址，保存包名
            utils.downloadFile(getIntent().getStringExtra("url"), "智能钢琴.apk", DownloadUtils.FileType.APK, Constents.APK_URL, null);
        } else {
            finish();
        }
    }


    @Override
    protected void setLinster() {

    }

}
