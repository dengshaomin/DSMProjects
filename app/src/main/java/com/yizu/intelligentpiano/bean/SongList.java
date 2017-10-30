package com.yizu.intelligentpiano.bean;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/21.
 * All Rights Reserved by YiZu
 */

public class SongList {

    /**
     * data : [{"id":"1","title":"儿童歌曲","addtime":"2017-09-20 14:31:36"},{"id":"2","title":"金典歌曲","addtime":"2017-09-20 14:31:53"}]
     * code : 000
     * message : 获取成功
     */

    private String code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * title : 儿童歌曲
         * addtime : 2017-09-20 14:31:36
         */

        private String id;
        private String title;
        private String addtime;

        public DataBean(String title) {
            this.title = title;
        }

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
}
