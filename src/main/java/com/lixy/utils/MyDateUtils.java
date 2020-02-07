package com.lixy.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class MyDateUtils {
    /**
     * 解析为日期，根据不同的类型
     * @param str
     * @param pattern
     * @return
     */
    public static Date parseDate(String str, String pattern){
        LocalDateTime local = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
        return Date.from( local.atZone( ZoneId.systemDefault()).toInstant());//转换成Date
    }

    public static String getCurrentDateStr(String pattern){
        //只能自定义模板获取这种格式的了
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDateStr("yyyy-MM-dd HH:mm:ss:SSS"));
    }

}
