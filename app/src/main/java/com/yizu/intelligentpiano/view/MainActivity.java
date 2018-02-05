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

import com.bumptech.glide.Glide;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.bean.Login;
import com.yizu.intelligentpiano.bean.QrCode;
import com.yizu.intelligentpiano.bean.VerSion;
import com.yizu.intelligentpiano.bean.WebSocketBean;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.ILogin;
import com.yizu.intelligentpiano.constens.ILogout;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.constens.IOpen;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;
import com.yizu.intelligentpiano.utils.SDCardUtils;
import com.yizu.intelligentpiano.utils.VersionUtils;

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
        getMac();
        if (PreManger.instance().getStatus().equals("2")) {
            button.setVisibility(View.VISIBLE);
        }
        if (PreManger.instance().getMacId().equals("")) {
            MyToast.ShowLong("无法获取设备ID");
            return;
        }
        cheackedPermisition();
        OkHttpUtils.getInstance().getOpen(new IOpen() {
            @Override
            public void open() {
                setDimension();
                updataAPP();
            }
        });
        OkHttpUtils.getInstance().startWebSocket();
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
            if (!MACID.equals("")) {
                PreManger.instance().saveMacId(MACID);
            }
        }
    }

    /**
     * 检查是否有写权限
     */
    private void cheackedPermisition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int mPermisition = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (mPermisition != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                SDCardUtils.creatFile();
            }
        } else {
            SDCardUtils.creatFile();
        }
    }

    /**
     * 设置二维码
     */
    private void setDimension() {
        String macId = PreManger.instance().getMacId();
        Map<String, String> map = new HashMap();
        map.put("scene", macId);
        OkHttpUtils.getInstance().postMap(HttpUrls.QRCODE, map, new IOkHttpCallBack() {
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
                    } else {
                        button.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void setLinster() {
        OkHttpUtils.getInstance().getLogin(new ILogin() {
            @Override
            public void login(final WebSocketBean.Datas datas) {
                MyToast.ShowLong("请求登录中...");
                Constents.user_id = datas.getUser_id();
                final Map<String, String> map1 = new HashMap<>();
                map1.put("user_id", datas.getUser_id());
                map1.put("device_id", datas.getDevice_id());
                map1.put("client_id", PreManger.instance().getClintId());
                OkHttpUtils.getInstance().postMap(HttpUrls.LOGIN, map1, new IOkHttpCallBack() {
                    @Override
                    public void success(String result) {
                        Login bean = OkHttpUtils.Json2Bean(result, Login.class);
                        if (bean.getCode().equals("000")) {
                            MyToast.ShowLong(bean.getMessage());
                            Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                            intent.putExtra("isWXLogin", true);
                            intent.putExtra("username", datas.getNickname());
                            intent.putExtra("pic", datas.getHeadimg());
                            startActivity(intent);
                        } else {
                            MyToast.ShowLong(bean.getMessage());
                        }
                    }
                });
            }
        });
        OkHttpUtils.getInstance().getLogout(new ILogout() {
            @Override
            public void logout() {
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
                SDCardUtils.creatFile();
            } else {
                MyToast.ShowLong("授权失败，请手动设置权限");
            }
        }
    }

    /**
     * 检查是否需要更新app
     */
    private void updataAPP() {
        OkHttpUtils.getInstance().postMap(HttpUrls.GETAPPVERSION, null, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                VerSion bean = OkHttpUtils.Json2Bean(result, VerSion.class);
                if (bean.getCode().equals("000")) {
                    if (VersionUtils.getVersionCode() < Integer.valueOf(bean.getDatas().getVersion_number())) {
                        Intent intent = new Intent(MainActivity.this, UpdataActivity.class);
                        intent.putExtra("url", bean.getDatas().getFileUrl());
                        startActivity(intent);
                    }
                } else {
                    MyToast.ShowLong("请求数据失败");
                }
            }
        });
    }

    /**
     * 请求退出
     */
    public void logout() {
        if (Constents.user_id.equals("")) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", Constents.user_id);
        map.put("device_id", PreManger.instance().getMacId());
        OkHttpUtils.getInstance().postMap(HttpUrls.LOGOUT, map, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Intent intent = new Intent(Constents.ACTION);
                intent.putExtra("what", Constents.LOGOUT_FINISH);
                MainActivity.this.sendBroadcast(intent);
                MyLogUtils.e(TAG, "app发出结束广播");
                MyToast.ShowLong("退出成功");
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
        if (broadcast != null) {
            unregisterReceiver(broadcast);
        }
        OkHttpUtils.getInstance().webCancle();
    }
}
