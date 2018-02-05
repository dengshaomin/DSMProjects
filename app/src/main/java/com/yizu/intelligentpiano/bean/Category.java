package com.yizu.intelligentpiano.bean;

import java.util.List;

/**
 * Author：Created by liuxiaozhu on 2017/12/16.
 * Email: chenhuixueba@163.com
 */

public class Category {
    private String code;
    private String message;
    private List<Song> data;

    public class Song {
        private String id;
        private String title;
        private String addtime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }
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

    public List<Song> getData() {
        return data;
    }

    public void setData(List<Song> data) {
        this.data = data;
    }
}
