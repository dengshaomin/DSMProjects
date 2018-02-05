package com.yizu.intelligentpiano.bean;

/**
 * Author：Created by liuxiaozhu on 2018/1/20.
 * Email: chenhuixueba@163.com
 */

public class WebSocketBean {
    private String type;
    private String device_id;
    private Datas data;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Datas getData() {
        return data;
    }

    public void setData(Datas data) {
        this.data = data;
    }

    public class Datas {
        //init
        private String client_id;
        //登录
        private String user_id;
        private String headimg;
        private String nickname;
        private String score;
        private String device_id;

        //推送
        private String music_id;
        private String music_title;
        private String auther;
        private String updatetime;
        private String file_xml;

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

        public String getAuther() {
            return auther;
        }

        public void setAuther(String auther) {
            this.auther = auther;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getFile_xml() {
            return file_xml;
        }

        public void setFile_xml(String file_xml) {
            this.file_xml = file_xml;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }
    }
}
