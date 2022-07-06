package cn.yoyo.redisdemo.tools;

import cn.yoyo.redisdemo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CommonTool {

    public static User getLoginUser(HttpServletRequest request, StringRedisTemplate stringRedisTemplate){
        //获取登入的用户信息
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieTool.findCookie(cookies, "currUser");
        User user = null;

        if (cookie!=null){
            try {
                String loginUserJson = stringRedisTemplate.opsForValue().get("loginUser:id:" + cookie.getValue());
                if (loginUserJson!=null){
                    user = new ObjectMapper().readValue(loginUserJson, User.class);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
