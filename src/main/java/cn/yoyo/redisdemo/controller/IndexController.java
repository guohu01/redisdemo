package cn.yoyo.redisdemo.controller;

import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.servlet.UserService;
import cn.yoyo.redisdemo.tools.CommonTool;
import cn.yoyo.redisdemo.tools.CookieTool;
import org.eclipse.jetty.client.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "index.html")
    public String index(HttpServletRequest request){
        User loginUser = CommonTool.getLoginUser(request, stringRedisTemplate);
        if (loginUser!=null){
            return "redirect:/home.html";
        }
        return "index";
    }

    /**
     * 接收注册的方法
     * @return
     */
    @PostMapping("/register.html")
    public String register(User user,String password2){
        //数据有效性验证   略。。
        System.out.println(user);
        if (!user.getPassword().equals(password2)){
            throw new RuntimeException("两次密码不相同");
        }
        //密码一致
        user.setCreateTime(new Date());
        userService.insert(user);
        return "redirect:/index.html";
    }

    /**
     * 登入表单提交
     * @return
     */
    @PostMapping(value = {"/login","login.html"})
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response){
        try {
            userService.login(username,password,response);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/index.html";
        }
        return "redirect:/home.html";
    }

    @GetMapping("/logout.html")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieTool.findCookie(cookies, "currUser");
        if (cookie!=null){
            //删除redis中的user信息
            stringRedisTemplate.delete("loginUser:id:"+cookie.getValue());
            //删除cookie中的userID
            cookie.setValue("");
            //Cookie生效到客户端
            response.addCookie(cookie);
        }
        return "redirect:/index.html";
    }
}
