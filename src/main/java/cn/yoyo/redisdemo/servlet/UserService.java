package cn.yoyo.redisdemo.servlet;

import cn.yoyo.redisdemo.model.Post;
import cn.yoyo.redisdemo.model.User;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    /**
     * 用户注册，插入用户
     * @param user
     */
    public void insert(User user);

    /**
     * 登入验证
     * @param username
     * @param password
     * @param response
     */
    public void login(String username, String password, HttpServletResponse response);

    /**
     * 根据id拿到用户信息
     * @param u
     * @return
     */
    public User getUserByUid(Integer u);

    public void insertUserFocus(Integer id, Integer focusuid);

    public List<Integer> getFollowStat(Integer id, Integer focusUserId);

    public void deleteUserFocus(Integer id, Integer focusUserId);

    public int getFs(Integer id);

    public int getFollows(Integer id);

    public List<User> getNewestUsers();
}
