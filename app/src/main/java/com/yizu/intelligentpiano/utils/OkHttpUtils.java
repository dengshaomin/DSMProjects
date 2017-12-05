package com.yizu.intelligentpiano.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by liuxiaozhu on 2017/8/5.
 * All Rights Reserved by YiZu
 */

public class OkHttpUtils {
    private final static String TAG = "OkHttpUtils";
    private static OkHttpClient okHttpClient;
    // 超时时间（一分钟）
    private static final int TIMEOUT = 1000 * 60;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Gson gson;
    private static OkHttpUtils mInstance = null;

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
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)// 设置超时时间（60s）
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
        }

    }


    /**
     * post请求(Map类型的数据)
     */
    public static void postMap(final String url, Map<String, String> map,
                               final IOkHttpCallBack callBack) {
        if (callBack == null) {
            return;
        }
        if (!Constents.isNetworkConnected) {
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
                        MyToast.ShowShort("获取数据失败，" + e.getMessage());
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

    public enum RequestType {
        HTTP,
        HTTPS
    }
}
