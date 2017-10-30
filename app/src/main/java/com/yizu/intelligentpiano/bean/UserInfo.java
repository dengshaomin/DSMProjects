package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/9/26.
 * All Rights Reserved by YiZu
 */

public class UserInfo {

    /**
     * data : {"nickname":"chenzhenhui","headimg":null,"score":"99997499","device_id":"DGR5552556","leftscore":"99997499"}
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
         * nickname : chenzhenhui
         * headimg : null
         * score : 99997499
         * device_id : DGR5552556
         * leftscore : 99997499
         */

        private String nickname;
        private Object headimg;
        private String score;
        private String device_id;
        private String leftscore;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Object getHeadimg() {
            return headimg;
        }

        public void setHeadimg(Object headimg) {
            this.headimg = headimg;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getLeftscore() {
            return leftscore;
        }

        public void setLeftscore(String leftscore) {
            this.leftscore = leftscore;
        }
    }
}
