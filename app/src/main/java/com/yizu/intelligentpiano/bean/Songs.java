package com.yizu.intelligentpiano.bean;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/9/21.
 * All Rights Reserved by YiZu
 */

public class Songs {

    /**
     * data : {"count":"21","list":[{"music_id":"24","title":"月亮之上","auther":"陈苹","music_xml":"http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"},{"music_id":"23","title":"我在想你","auther":"刘海","music_xml":"http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"}]}
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
         * count : 21
         * list : [{"music_id":"24","title":"月亮之上","auther":"陈苹","music_xml":"http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"},{"music_id":"23","title":"我在想你","auther":"刘海","music_xml":"http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml"}]
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
             * music_id : 24
             * title : 月亮之上
             * auther : 陈苹
             * music_xml : http://piano.sinotransfer.com/Uploads/Download/2017-09-20/59c21068ef4b5.xml
             */

            private String music_id;
            private String title;
            private String auther;
            private String video_xml;
            private String music_xml;
            private String category_id;

            public String getMusic_id() {
                return music_id;
            }

            public void setMusic_id(String music_id) {
                this.music_id = music_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAuther() {
                return auther;
            }

            public void setAuther(String auther) {
                this.auther = auther;
            }

            public String getMusic_xml() {
                return music_xml;
            }

            public void setMusic_xml(String music_xml) {
                this.music_xml = music_xml;
            }

            public String getCategory_id() {
                return category_id;
            }

            public void setCategory_id(String category_id) {
                this.category_id = category_id;
            }

            public String getVideo_xml() {
                return video_xml;
            }

            public void setVideo_xml(String video_xml) {
                this.video_xml = video_xml;
            }
        }
    }
}
