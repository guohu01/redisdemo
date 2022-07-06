package cn.yoyo.redisdemo.dao;

import cn.yoyo.redisdemo.model.User;

import java.util.Date;
import java.util.List;

public interface UserDao {
    /**
     * 插入新用户
     */
    public void insert(User user);

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User getByUsername(String username);

    /**
     * 根据用户id获取user
     * @param uid
     * @return
     */
    public User getUserByUid(Integer uid);

    /**
     * 查询最新的n个用户
     * @return
     */
    public List<User> getNewestUsers();

}
