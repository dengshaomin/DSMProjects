package com.yizu.intelligentpiano.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.yizu.intelligentpiano.bean.WebSocketBean;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.ILogin;
import com.yizu.intelligentpiano.constens.ILogout;
import com.yizu.intelligentpiano.constens.IMusic;
import com.yizu.intelligentpiano.constens.INetStatus;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.constens.IOpen;
import com.yizu.intelligentpiano.constens.ITimeNot;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * Created by liuxiaozhu on 2017/8/5.
 * All Rights Reserved by YiZu
 */

public class OkHttpUtils extends WebSocketListener {
    private final static String TAG = "OkHttpUtils";
    private static OkHttpClient okHttpClient;
    // 超时时间（10S）
    private static final int TIMEOUT = 15;

    private Handler handler = new Handler(Looper.getMainLooper());
    private static Gson gson;
    private static OkHttpUtils mInstance = null;
    private WebSocket mWebSocket;
    private ILogin mLogin;
    private IMusic mMusic;
    private ILogout mLogout;
    private ITimeNot mTimeNot;
    private INetStatus mINetStatus;
    private IOpen mIOpen;
    private OkHttpClient mClient;
    private Request mRequest;

    public static synchronized OkHttpUtils getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }

    private OkHttpUtils() {
    }

    /**
     * 初始化OkHttpClient
     *
     * @param type 请求类型
     */
    public void init(RequestType type) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)// 设置超时时间（10s）
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS);
            if (type == RequestType.HTTPS) {
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;//跳过https安全认证
                    }
                });
            }
            okHttpClient = builder.build();
            gson = new Gson();
            mClient = new OkHttpClient.Builder()
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            mRequest = new Request.Builder()
                    .url(HttpUrls.WEB_Url)
                    .build();
        }

    }


    /**
     * post请求(Map类型的数据)
     */
    public void postMap(final String url, Map<String, String> map,
                        final IOkHttpCallBack callBack) {
        if (callBack == null) {
            return;
        }
        MyLogUtils.e(TAG, HttpUrls.HTTPBASE.concat(url));
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();

        final Request request = new Request.Builder()
                .url(HttpUrls.HTTPBASE.concat(url))
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.ShowShort("获取数据失败");
                        MyLogUtils.e(TAG, "Error，" + e.getMessage());
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = null;
                try {
                    result = response.body().string();
                    MyLogUtils.e(TAG, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalResult = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalResult == null || finalResult.equals("")) {
                            MyToast.ShowShort("获取数据为空");
                            return;
                        }
                        if (response.isSuccessful()) {
                            //获取成功，抛出数据
                            callBack.success(finalResult);
                        } else {
                            MyToast.ShowShort("获取数据失败，" + response.message());
                            MyLogUtils.e(TAG, "获取数据失败，" + response.message());
                        }
                    }
                });
            }
        });

    }

    /**
     * json转成bean类型
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T Json2Bean(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * json转成bean类型
     *
     * @return
     */
    public static String Bean2Json(Object o) {
        return gson.toJson(o);
    }

    public enum RequestType {
        HTTP,
        HTTPS
    }

    public void startWebSocket() {
        if (mWebSocket == null) mClient.newWebSocket(mRequest, this);
    }

    /**
     * 取消链接
     */
    public void webCancle() {
        if (null != mWebSocket) {
            mWebSocket.close(1000, "主动关闭");
            mWebSocket = null;
        }
    }

    /**
     * 获取登陆
     *
     * @param login
     */
    public void getLogin(ILogin login) {
        if (login == null) {
            throw new RuntimeException("ILogin不能为空");
        }
        mLogin = login;
    }

    /**
     * 推送
     *
     * @param music
     */
    public void getMusic(IMusic music) {
        if (music == null) {
            throw new RuntimeException("IMusic不能为空");
        }
        mMusic = music;
    }

    /**
     * 退出
     *
     * @param logout
     */
    public void getLogout(ILogout logout) {
        if (logout == null) {
            throw new RuntimeException("ILogout不能为空");
        }
        mLogout = logout;
    }

    public void getTimeNot(ITimeNot iTimeNot) {
        if (null == iTimeNot) {
            throw new RuntimeException("ITimeNot不能为空");
        }
        mTimeNot = iTimeNot;
    }

    public void getNetStatus(INetStatus netStatus) {
        if (netStatus != null) {
            mINetStatus = netStatus;
        }
    }

    public void getOpen(IOpen open) {
        if (open != null) {
            this.mIOpen = open;
        }
    }

    /**
     * ***********************长连接回调***********************
     */
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocket = webSocket;
        MyLogUtils.e(TAG, "onOpen");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mIOpen!=null)mIOpen.open();
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        MyLogUtils.e(TAG, "onMessage:" + text);
        final WebSocketBean bean = OkHttpUtils.Json2Bean(text, WebSocketBean.class);
        WebSocketBean sendBean = new WebSocketBean();
        if (mWebSocket != null) {
            switch (bean.getType()) {
                case "ping":
                    sendBean.setType("pong");
                    String ping = OkHttpUtils.Bean2Json(sendBean);
                    mWebSocket.send(ping);
                    MyLogUtils.e(TAG, "发送ping" + ping);
                    break;
                case "init":
                    if (bean.getData() != null && bean.getData().getClient_id() != null) {
                        PreManger.instance().setClintId(bean.getData().getClient_id());
                    }
                    sendBean.setType("init");
                    sendBean.setDevice_id(PreManger.instance().getMacId());
                    String init = OkHttpUtils.Bean2Json(sendBean);
                    MyLogUtils.e(TAG, "发送init" + init);
                    mWebSocket.send(init);
                    break;
                case "login":
                    //登陆
                    if (mLogin != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLogin.login(bean.getData());
                            }
                        });
                    }
                    break;
                case "music":
                    //音乐推送
                    if (mMusic != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mMusic.music(bean.getData());
                            }
                        });

                    }
                    break;
                case "logout":
                    //小程序退出
                    if (mLogout != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLogout.logout();
                            }
                        });

                    }
                    break;
                case "leftscore":
                    if (mTimeNot != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTimeNot.notTime();
                            }
                        });
                    }
                    break;
            }
        }

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        MyLogUtils.e(TAG, "onClosing");
        MyLogUtils.e(TAG, "code:" + code + " reason:" + reason);

    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        MyLogUtils.e(TAG, "onClosed");
        MyLogUtils.e(TAG, "code:" + code + " reason:" + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, final Throwable t, Response response) {
        //出现异常会进入此回调
        MyLogUtils.e(TAG, "onFailure");
        MyLogUtils.e(TAG, "throwable:" + t.toString());
        MyLogUtils.e(TAG, "response:" + response);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mINetStatus != null) {
                    mINetStatus.isNoNet();
                }
//                mWebSocket = null;
//                if (!t.toString().equals("java.net.SocketException: recvfrom failed: ETIMEDOUT (Connection timed out)")) {
//                    startWebSocket();
//                }
            }
        });
    }
}
