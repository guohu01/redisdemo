package cn.yoyo.redisdemo.servlet;

import cn.yoyo.redisdemo.dao.PostDao;
import cn.yoyo.redisdemo.model.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("postServlet")
public class PostServiceImpl implements PostService {
    @Autowired
    private PostDao postDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void insert(Post post) {
        postDao.insert(post);
        //添加完数据以后，删除redis里面缓存   实现缓存同步
        stringRedisTemplate.opsForHash().delete("content_list","profile:id:"+post.getUser().getId(),"home:id:"+post.getUser().getId());
    }

    @Override
    public List<Post> getPostByUid(Integer id) {
        List<Post> postList = null;
        ObjectMapper mapper = new ObjectMapper();
        String postListJson = null;
        //1.先看redis里面有没有数据
        postListJson = (String) stringRedisTemplate.opsForHash().get("content_list","profile:id:"+id);
        if (StringUtils.isEmpty(postListJson)){
            //2.没有，从mysql中取数据
            System.out.println("---------------profile从mysql中取数据--------------");
            postList = postDao.getPostByUid(id);
            //数据库查询为空，返回"[]"
            if (postList.size()==0) return null;
            try {
                postListJson = mapper.writeValueAsString(postList);
                //3.将从mysql中取出的数据放入redis
                stringRedisTemplate.opsForHash().put("content_list","profile:id:"+id,postListJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            //redis中有数据
            System.out.println("--------------profile从redis中取数据--------------");
            try {
                postList = mapper.readValue(postListJson, new TypeReference<List<Post>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        System.out.println(postList);
        return postList;
    }

    @Override
    public List<Post> getHomePost(Integer id) {
        List<Post> postList = null;
        ObjectMapper mapper = new ObjectMapper();
        String postListJson = null;
        //1.先看redis里面有没有数据
        postListJson = (String) stringRedisTemplate.opsForHash().get("content_list","home:id:"+id);
        if (StringUtils.isEmpty(postListJson)){
            //2.没有，从mysql中取数据
            System.out.println("---------------home从mysql中取数据--------------");
            postList = postDao.getHomePost(id);
            //数据库查询为空，返回"[]"
            if (postList.size()==0) return null;
            try {
                postListJson = mapper.writeValueAsString(postList);
                //3.将从mysql中取出的数据放入redis
                stringRedisTemplate.opsForHash().put("content_list","home:id:"+id,postListJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            //redis中有数据
            System.out.println("--------------home从redis中取数据--------------");
            try {
                postList = mapper.readValue(postListJson, new TypeReference<List<Post>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        System.out.println(postList);
        return postList;
    }

    @Override
    public List<Post> getNewestPosts() {
        List<Post> postList = null;
        ObjectMapper mapper = new ObjectMapper();
        String postListJson = null;
        //1.先看redis里面有没有数据
        postListJson = stringRedisTemplate.opsForValue().get("timeline:newestPosts");
        if (StringUtils.isEmpty(postListJson)){
            //2.没有，从mysql中取数据
            System.out.println("---------------timeline:Posts:从mysql中取数据--------------");
            postList = postDao.getNewestPosts();
            //数据库查询为空，返回"[]"
//            if (postList.size()==0) return null;
            try {
                postListJson = mapper.writeValueAsString(postList);
                //3.将从mysql中取出的数据放入redis
                stringRedisTemplate.opsForValue().set("timeline:newestPosts",postListJson);
                stringRedisTemplate.expire("timeline:newestPosts",60*1, TimeUnit.SECONDS);


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            //redis中有数据
            System.out.println("--------------timeline:Posts:从redis中取数据--------------");
            try {
                postList = mapper.readValue(postListJson, new TypeReference<List<Post>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        System.out.println(postList);
        return postList;
    }
}
