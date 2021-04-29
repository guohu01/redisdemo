package cn.yoyo.redisdemo.controller;

import cn.yoyo.redisdemo.model.Post;
import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.servlet.PostService;
import cn.yoyo.redisdemo.servlet.UserService;
import cn.yoyo.redisdemo.tools.CommonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "profile.html")
    public String profile(Integer u, Model model,HttpServletRequest request){

        //1.拿到传过来的uid
        User focusUser = userService.getUserByUid(u);
        model.addAttribute("user",focusUser);
        //拿到登入用户
        User loginUser = CommonTool.getLoginUser(request, stringRedisTemplate);

        //判断当前登入的用户与目标用户的直接关系
        List<Integer> followStatList = userService.getFollowStat(loginUser.getId(),focusUser.getId());
        boolean fsFlag = false;
        for (int i = 0;i < followStatList.size();i++){
            if (followStatList.get(i)==1){
                //当前用户关注了目标用户
                fsFlag = true;
                break;
            }
        }
        if (fsFlag){
            model.addAttribute("fsbtnValue","取消关注ta");
            //关注状态  1：已经关注
            model.addAttribute("f","1");
        }else {
            model.addAttribute("fsbtnValue","关注ta");
            model.addAttribute("f","0");
        }

        //显示个人主页下的微博信息
        List<Post> postList =  postService.getPostByUid(focusUser.getId());
        model.addAttribute("postList",postList);

        return "profile";
    }

    @GetMapping("/follow.html")
    public String follow(Integer focusuid,Integer f, HttpServletRequest request){
        //1.建立两个用户的关注关系
        //2.拿到当前用户的登入信息
        User focusUser = userService.getUserByUid(focusuid);
        if (focusUser==null)
            return "redirect:/profile.html?u="+focusuid;

        User loginUser = CommonTool.getLoginUser(request, stringRedisTemplate);
        if (f==0){
            //还没关注，建立关系
            userService.insertUserFocus(loginUser.getId(),focusUser.getId());
        }else {
            userService.deleteUserFocus(loginUser.getId(),focusUser.getId());
        }
        return "redirect:/profile.html?u="+focusuid;
    }
}
