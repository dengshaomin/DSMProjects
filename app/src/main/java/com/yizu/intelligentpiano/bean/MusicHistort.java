package com.yizu.intelligentpiano.bean;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/26.
 * All Rights Reserved by YiZu
 */

public class MusicHistort {

    /**
     * data : {"list":[{"auther":"邹浩","music_id":"6","music_title":"月亮之上","score":"80"},{"auther":"刘海","music_id":"7","music_title":"月亮之上","score":"80"},{"auther":"陈苹","music_id":"8","music_title":"月亮之上","score":"80"}],"count":"3"}
     * code : 000
     * message : 获取成功
     */

    private DataBean data;
    private String code;
    private String message;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * list : [{"auther":"邹浩","music_id":"6","music_title":"月亮之上","score":"80"},{"auther":"刘海","music_id":"7","music_title":"月亮之上","score":"80"},{"auther":"陈苹","music_id":"8","music_title":"月亮之上","score":"80"}]
         * count : 3
         */

        private String count;
        private List<ListBean> list;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * auther : 邹浩
             * music_id : 6
             * music_title : 月亮之上
             * score : 80
             */

            private String auther;
            private String music_id;
            private String music_title;
            private String score;

            public String getAuther() {
                return auther;
            }

            public void setAuther(String auther) {
                this.auther = auther;
            }

            public String getMusic_id() {
                return music_id;
            }

            public void setMusic_id(String music_id) {
                this.music_id = music_id;
            }

            public String getMusic_title() {
                return music_title;
            }

            public void setMusic_title(String music_title) {
                this.music_title = music_title;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }
        }
    }
}
