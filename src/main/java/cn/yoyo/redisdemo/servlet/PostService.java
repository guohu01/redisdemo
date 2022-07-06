package cn.yoyo.redisdemo.servlet;

import cn.yoyo.redisdemo.model.Post;

import java.util.List;

public interface PostService {
    public void insert(Post post);

    public List<Post> getPostByUid(Integer id);

    public List<Post> getHomePost(Integer id);

    public List<Post> getNewestPosts();
}
