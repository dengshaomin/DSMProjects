package com.yizu.intelligentpiano.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CommonCallback;
import com.bumptech.glide.Glide;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.appliction.MyAppliction;
import com.yizu.intelligentpiano.bean.Login;
import com.yizu.intelligentpiano.bean.QrCode;
import com.yizu.intelligentpiano.bean.VerSion;
import com.yizu.intelligentpiano.broadcast.MyMessageReceiver;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.ILogin;
import com.yizu.intelligentpiano.constens.ILogout;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.VersionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆界面,主界面
 */
public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private TextView button;
    private ImageView dimension;
    private MyBroadcastReceiver broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        button = (TextView) findViewById(R.id.login_button);
        dimension = (ImageView) findViewById(R.id.dimension);
    }

    @Override
    protected void setData() {
        //注册广播
        setRegisterReceiver();
        if (PreManger.instance().getStatus().equals("2")) {
            button.setVisibility(View.VISIBLE);
        }

        button.setSelected(true);
        cheackedPermisition();
    }

    private void setRegisterReceiver() {
        broadcast = new MyBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter(Constents.ACTION);
        registerReceiver(broadcast, iFilter);
    }

    /**
     * 获取mac地址
     */
    private void getMac() {
        if (PreManger.instance().getMacId().equals("")) {
            String MACID = VersionUtils.getMacAddress();
            if (!MACID.equals("02:00:00:00:00:02")) {
                PreManger.instance().saveMacId(MACID);
                //设置别名
                if (MyAppliction.pushService != null) {
                    MyAppliction.pushService.addAlias(MACID, new CommonCallback() {
                        @Override
                        public void onSuccess(String s) {
                            MyLogUtils.e(TAG, "别名设置成功");
                        }

                        @Override
                        public void onFailed(String s, String s1) {
                            MyLogUtils.e(TAG, "别名设置失败" + s + "    " + s1);
                        }
                    });
                }
            } else {
                MyToast.ShowLong("MAC地址获取失败,无法生成二维码");
            }
        } else {
            if (MyAppliction.pushService != null) {
                MyAppliction.pushService.addAlias(PreManger.instance().getMacId(), new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        MyLogUtils.e(TAG, "别名设置成功");
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        MyLogUtils.e(TAG, "别名设置失败");
                    }
                });
            }
        }

    }

    /**
     * 检查是否有写权限
     */
    private void cheackedPermisition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int mPermisition = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            如果没有位置权限
            if (mPermisition != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                startActivity(new Intent(MainActivity.this,Piano2Activity.class));
                if (Constents.isNetworkConnected) {
                    updataAPP();
                }
            }
        } else {
            if (Constents.isNetworkConnected) {
                updataAPP();
            }
        }
    }

    /**
     * 设置二维码
     */
    private void setDimension() {
        String macId = PreManger.instance().getMacId();
        if (PreManger.instance().getPic().equals("") && !(macId.equals(""))) {
            MyLogUtils.e("UUID", macId);
            Map<String, String> map = new HashMap();
            map.put("scene", macId);
            OkHttpUtils.postMap(HttpUrls.QRCODE, map, new IOkHttpCallBack() {
                @Override
                public void success(String result) {
                    QrCode bean = OkHttpUtils.Json2Bean(result, QrCode.class);
                    if (bean.getCode().equals("000")) {
                        PreManger.instance().saveData(bean.getData().getImgurl(), bean.getData().getStatus());
                        Glide.with(MainActivity.this).load(bean.getData().getImgurl()).into(dimension);
                        if (bean.getData().getStatus().equals("2")) {
                            button.setVisibility(View.VISIBLE);
                            PreManger.instance().saveUserInfo(bean.getData().getUser_id(),
                                    bean.getData().getHeadimg(),
                                    bean.getData().getNickname());
                        }
                    }
                }
            });
        } else {
            if (!PreManger.instance().getPic().equals("")) {
                Glide.with(this).load(PreManger.instance().getPic()).into(dimension);
            }
        }
    }

    @Override
    protected void setLinster() {
        //推送登陆信息
        MyMessageReceiver.getLogin(new ILogin() {
            @Override
            public void login(final Map<String, String> map) {
                if (PreManger.instance().getMacId().equals("")) {
                    MyLogUtils.e(TAG, "Mac地址为空");
                    return;
                }
                if (Constents.isNetworkConnected) {
                    final Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("user_id", map.get("user_id"));
                    map1.put("device_id", PreManger.instance().getMacId());
                    OkHttpUtils.postMap(HttpUrls.LOGIN, map1, new IOkHttpCallBack() {
                        @Override
                        public void success(String result) {
                            Login bean = OkHttpUtils.Json2Bean(result, Login.class);
                            if (bean.getCode().equals("000")) {
                                Constents.user_id = map.get("user_id");
                                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                                intent.putExtra("isWXLogin", true);
                                startActivity(intent);
                            } else if (bean.getCode().equals("108")) {
                                MyToast.ShowLong(bean.getMessage());
                            } else if (bean.getCode().equals("103")) {
//                                未推出
                                MyToast.ShowLong(bean.getMessage());
                            }
                        }
                    });
                }
            }
        });
        MyMessageReceiver.getLogout(new ILogout() {
            @Override
            public void logout() {
                //发送activity可以结束的广播
                Intent intent = new Intent(Constents.ACTION);
                intent.putExtra("what", Constents.LOGOUT_FINISH);
                MainActivity.this.sendBroadcast(intent);
                MyLogUtils.e(TAG, "小程序发出结束广播，app分发");
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && PreManger.instance().getStatus().equals("2") && !PreManger.instance().getMacId().equals("")) {
            //确定
            MyLogUtils.e(TAG, "确定");
            Constents.user_id = PreManger.instance().getUserID();
            startActivity(new Intent(this, SelectActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyToast.ShowLong("授权成功");
                if (Constents.isNetworkConnected) {
                    updataAPP();
                }
            } else {
                MyToast.ShowLong("授权失败，请手动设置权限");
            }
        }
    }

    /**
     * 检查是否需要更新app
     */
    private void updataAPP() {
        creatFile();
        OkHttpUtils.postMap(HttpUrls.GETAPPVERSION, null, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                VerSion bean = OkHttpUtils.Json2Bean(result, VerSion.class);
                if (bean.getCode().equals("000")) {
                    if (VersionUtils.getVersionCode() != Integer.valueOf(bean.getDatas().getVersion_number())) {
                        MyLogUtils.e("downloadurl", bean.getDatas().getFileUrl());
                        Intent intent = new Intent(MainActivity.this, UpdataActivity.class);
                        intent.putExtra("url", bean.getDatas().getFileUrl());
                        startActivity(intent);
                    }
                } else {
                    MyToast.ShowLong("请求数据失败");
                }
            }
        });
        getMac();
        setDimension();
    }

    /**
     * 创建文件夹
     */
    private void creatFile() {
        String sd = SDCardUtils.getExternalStorageDirectory();
        if (!sd.equals("")) {
            //智能钢琴
            File piano = new File(sd.concat(Constents.PIANO_URL));
            if (!piano.exists()) {
                piano.mkdirs();
            }
            //apk
            File apk = new File(sd.concat(Constents.APK_URL));
            if (!apk.exists()) {
                apk.mkdirs();
            }
            //video
            File video = new File(sd.concat(Constents.VIDEO_URL));
            if (!video.exists()) {
                video.mkdirs();
            }
            //儿歌
            File children = new File(sd.concat(Constents.XML_CHILDREN));
            if (!children.exists()) {
                children.mkdirs();
            }
            //金典
            File satine = new File(sd.concat(Constents.XML_SATINE));
            if (!satine.exists()) {
                satine.mkdirs();
            }
            //怀古
            File nostalgic = new File(sd.concat(Constents.XML_NOSTALGIC));
            if (!nostalgic.exists()) {
                nostalgic.mkdirs();
            }
            //流行
            File popular = new File(sd.concat(Constents.XML_POPULAR));
            if (!popular.exists()) {
                popular.mkdirs();
            }
            //动漫游戏
            File game = new File(sd.concat(Constents.XML_GAME));
            if (!game.exists()) {
                game.mkdirs();
            }
            //伤感
            File sentimental = new File(sd.concat(Constents.XML_SENTIMENTAL));
            if (!sentimental.exists()) {
                sentimental.mkdirs();
            }
        }
    }

    /**
     * 微信小程序登陆的时候退出
     */
    public void logout() {
        if (Constents.user_id.equals("")) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", Constents.user_id);
        map.put("device_id", PreManger.instance().getMacId());
        OkHttpUtils.postMap(HttpUrls.LOGOUT, map, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Login bean = OkHttpUtils.Json2Bean(result, Login.class);
                if (bean.getCode().equals("000")) {
                    //是微信小程序登陆退出
                    //发送activity可以结束的广播
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra("what", Constents.LOGOUT_FINISH);
                    MainActivity.this.sendBroadcast(intent);
                    MyLogUtils.e(TAG, "app发出结束广播，app分发");
                } else {
//                    logout();
                }
            }
        });
    }

    /**
     * 广播接收器
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT:
                    //主动请求退出
                    logout();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }
}
