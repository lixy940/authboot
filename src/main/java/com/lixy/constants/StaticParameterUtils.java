package com.lixy.constants;


import com.lixy.utils.DBPropertyReaderUtils;

/**
 * Author：MR LIS，2019/10/21
 * Copyright(C) 2019 All rights reserved.
 */
public final class StaticParameterUtils {
    /**
     * 安全密码(UUID生成)，作为盐值用于用户密码的加密
     */
    public static final String ZYD_SECURITY_KEY = "929123f8f17944e8b0a531045453e1f1";

    /**
     * token header
     */
    public static final String HTTP_HEADER_TOKEN = "";


    public static final String SERVER_PORT = DBPropertyReaderUtils.getProValue("server.port");
    /**
     * login url
     */
    public static final String PASSPORT_LOGIN_URI = "/passport/login";
    /**
     * logout url
     */
    public static final String PASSPORT_LOGOUT_URI = "/passport/logout";
    /**
     * signin  url
     */
    public static final String PASSPORT_SIGNIN_URI = "/passport/signin";

}
