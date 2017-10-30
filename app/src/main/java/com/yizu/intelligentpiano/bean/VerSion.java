package com.yizu.intelligentpiano.bean;

/**
 * Created by liuxiaozhu on 2017/9/22.
 * All Rights Reserved by YiZu
 */

public class VerSion {
    private String code;
    private String message;
    private VerSionBase data;

    public class VerSionBase {
        private String id;
        private String title;
        private String version_number;
        private String version_name;
        private String file;
        private String addtime;

        public String getmId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getVersion_number() {
            return version_number;
        }

        public String getVersion_name() {
            return version_name;
        }

        public String getFileUrl() {
            return file;
        }

        public String getAddtime() {
            return addtime;
        }
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public VerSionBase getDatas() {
        return data;
    }
}
