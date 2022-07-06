package cn.yoyo.redisdemo.web;

import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.tools.CookieTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    /**
     * 构造方法注入
     * @param stringRedisTemplate
     */
    public AuthInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 在请求处理之前调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieTool.findCookie(cookies, "currUser");
        if (cookie!=null){
            //已经登入，查看是否登入过期

            String userStr = null;
            userStr = stringRedisTemplate.opsForValue().get("loginUser:id:" + cookie.getValue());

            if (userStr!=null){
                //登入成功状态
                return true;
            }else{
                //登入过期
                System.out.println("登入已过期");
                response.sendRedirect("/index.html");
                return false;
            }
        }else{
            //没有登入
            response.sendRedirect("/index.html");
            return false;
        }
    }
}
