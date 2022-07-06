package cn.yoyo.redisdemo.dao;

import cn.yoyo.redisdemo.model.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostDao {
    public void insert(Post post);

    public List<Post> getPostByUid(@Param("id") Integer id);

    /**
     *根据登入的用户id，获取此用户的发布信息和此用户关注的用户的发布信息
     * @param id
     * @return
     */
    public List<Post> getHomePost(Integer id);

    public List<Post> getNewestPosts();
}
