package cn.yoyo.redisdemo.controller;

import cn.yoyo.redisdemo.model.Post;
import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.servlet.PostService;
import cn.yoyo.redisdemo.servlet.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TimelineController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping(value = "timeline.html")
    public String timeline(Model model){

        //显示最新的用户
        List<User> newestUsers = userService.getNewestUsers();
        model.addAttribute("newestUsers",newestUsers);
        //最新的20条微博
        List<Post> newestPosts = postService.getNewestPosts();
        model.addAttribute("newestPosts",newestPosts);
        return "timeline";
    }
}
