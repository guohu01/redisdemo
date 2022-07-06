package cn.yoyo.redisdemo.servlet;

import cn.yoyo.redisdemo.dao.UserDao;
import cn.yoyo.redisdemo.dao.UserFocusDao;
import cn.yoyo.redisdemo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserFocusDao userFocusDao;

    //操作redis
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public void login(String username, String password, HttpServletResponse response) {
        User user = userDao.getByUsername(username);
        //1.判断用户名是否存在
        if (user==null){
            throw new RuntimeException("用户名或密码有误1");
        }
        //2.判断密码是否正确
        if (!password.equals(user.getPassword())){
            throw new RuntimeException("用户名或密码有误2");
        }
        //3.登入成功，将用户名密码保存到redis
        ObjectMapper mapper = new ObjectMapper();
        String value = null;
        try {
            value = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("json解析异常");
        }
        //4.设置用户状态时效性
        stringRedisTemplate.opsForValue().set("loginUser:id:"+user.getId(),value);
        stringRedisTemplate.expire("loginUser:id:" + user.getId(), 60*5, TimeUnit.SECONDS);
        //5.最后把userId放入cookie中
        Cookie cookie = new Cookie("currUser",user.getId()+"");
        response.addCookie(cookie);  //把cookie写到客户端的浏览器
    }

    @Override
    public User getUserByUid(Integer u) {
        User user = userDao.getUserByUid(u);
        return user;
    }

    @Override
    public void insertUserFocus(Integer id, Integer focusuid) {
        userFocusDao.insert(id,focusuid,new Date());

        //同步缓存
        stringRedisTemplate.delete("home:id:"+id+":follows");
        stringRedisTemplate.delete("home:id:"+focusuid+":fs");
    }

    @Override
    public List<Integer> getFollowStat(Integer id, Integer focusUserId) {
        return userFocusDao.getFollowStat(id,focusUserId);
    }

    @Override
    public void deleteUserFocus(Integer id, Integer focusUserId) {
        userFocusDao.delete(id,focusUserId);
        //同步缓存
        stringRedisTemplate.delete("home:id:"+id+":follows");
        stringRedisTemplate.delete("home:id:"+focusUserId+":fs");
    }

    @Override
    public int getFs(Integer id) {
        //先从redis中取数据
        int fs;
        String fsStr = stringRedisTemplate.opsForValue().get("home:id:"+id+":fs");
        if (StringUtils.isEmpty(fsStr)){
            //redis中取出数据为空，从mysql中取
            System.out.println("------------fs:从mysql------------");
            fs = userFocusDao.getFs(id);
            //存redis中
            stringRedisTemplate.opsForValue().set("home:id:"+id+":fs", fs+"");
        }else{
            System.out.println("------------fs:从redis------------");
            fs = Integer.parseInt(fsStr);
        }
        return fs;
    }

    @Override
    public int getFollows(Integer id) {
        //先从redis中取数据
        int follows;
        String fsStr = stringRedisTemplate.opsForValue().get("home:id:"+id+":follows");
        if (StringUtils.isEmpty(fsStr)){
            //redis中取出数据为空，从mysql中取
            System.out.println("------------follows:从mysql------------");
            follows = userFocusDao.getFollows(id);
            //存redis中
            stringRedisTemplate.opsForValue().set("home:id:"+id+":follows", follows+"");
        }else{
            System.out.println("------------follows:从redis------------");
            follows = Integer.parseInt(fsStr);
        }
        return follows;
    }

    @Override
    public List<User> getNewestUsers() {
        List<User> nUsers = null;
        ObjectMapper mapper = new ObjectMapper();
        //先从redis里面去取
        String newestUsers = stringRedisTemplate.opsForValue().get("timeline:newestUsers");
        if (StringUtils.isEmpty(newestUsers)){
            System.out.println("------------timeline:Users:从mysql-------------");
            //redis为空，从mysql中取
            nUsers = userDao.getNewestUsers();
            //放入redis
            if (nUsers!=null) {
                try {
                    newestUsers = mapper.writeValueAsString(nUsers);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            stringRedisTemplate.opsForValue().set("timeline:newestUsers",newestUsers);
            stringRedisTemplate.expire("timeline:newestUsers",60*1, TimeUnit.SECONDS);
        }else{
            System.out.println("-------------timeline:Users:从redis-------------");
            try {
                nUsers = mapper.readValue(newestUsers, new TypeReference<List<User>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return nUsers;
    }
}
