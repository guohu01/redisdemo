package cn.yoyo.redisdemo;

import cn.yoyo.redisdemo.model.User;
import cn.yoyo.redisdemo.servlet.UserService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

@SpringBootTest
class RedisdemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
//        stringRedisTemplate.opsForValue().set("name","zhangs22");
        User user = new User();
        user.setUsername("aaa");
        user.setPassword("1212");
        user.setCreateTime(new Date());
        userService.insert(user);
    }

}
