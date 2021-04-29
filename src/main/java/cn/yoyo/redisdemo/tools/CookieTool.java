package cn.yoyo.redisdemo.tools;

import javax.servlet.http.Cookie;

/**
 * 判断cookies中是否存在单前键为key的cookie
 * 存在，返回
 */
public class CookieTool {

    public static Cookie findCookie(Cookie[] cookies, String key ){
        if (cookies!=null && cookies.length>0){
            for (int i = 0;i<cookies.length;i++){
                if (cookies[i].getName().equals(key)){
                    return cookies[i];
                }
            }
        }
        return null;
    }
}
