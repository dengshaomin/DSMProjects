package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2018/1/11.
 * Email: chenhuixueba@163.com
 */

public class OneSong {
    private Songs.DataBean.ListBean data;
    private String code;
    private String message;

    public OneSong(Songs.DataBean.ListBean data, String code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public Songs.DataBean.ListBean getData() {
        return data;
    }

    public void setData(Songs.DataBean.ListBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
