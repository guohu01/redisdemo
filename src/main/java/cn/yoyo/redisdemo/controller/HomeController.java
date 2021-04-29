package cn.yoyo.redisdemo.controller;

import cn.yoyo.redisdemo.dao.UserDao;
import cn.yoyo.redisdemo.model.Post;
import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.servlet.PostService;
import cn.yoyo.redisdemo.servlet.UserService;
import cn.yoyo.redisdemo.tools.CommonTool;
import cn.yoyo.redisdemo.tools.CookieTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostService postService;

    @GetMapping(value = {"home","/home.html"})
    public String home(HttpServletRequest request, Model model){
        //获取登入的用户信息
        User user = CommonTool.getLoginUser(request, stringRedisTemplate);
        if (user!=null){
            model.addAttribute("loginUser",user);
        }
        //实现下面显示的微博信息
        List<Post> homePostList = postService.getHomePost(user.getId());
        model.addAttribute("homePostList",homePostList);

        //显示粉丝数和关注数
        int fs = userService.getFs(user.getId());
        int follows = userService.getFollows(user.getId());
        model.addAttribute("fs",fs);
        model.addAttribute("follows",follows);

        return "home";
    }

    @PostMapping("post.html")
    public String post(Post post, HttpServletRequest request){
        //接收信息,插入到数据库
        User loginUser = CommonTool.getLoginUser(request, stringRedisTemplate);
        if (loginUser==null){
            System.out.println("登入已过期");
            return "redirect:/index.html";
        }
        post.setUser(loginUser);
        post.setCreateTime(new Date());
        postService.insert(post);
        return "redirect:/home.html";
    }

}
