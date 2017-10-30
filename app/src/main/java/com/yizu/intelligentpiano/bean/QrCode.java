package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/9/25.
 * All Rights Reserved by YiZu
 */

public class QrCode {

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

    public  class DataBean {

        private String status;
        private String imgurl;
        private String user_id;
        private String nickname;
        private String headimg;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getHeadimg() {
            return headimg;
        }
    }
}
