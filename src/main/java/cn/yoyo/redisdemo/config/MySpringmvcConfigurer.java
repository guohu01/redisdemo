package cn.yoyo.redisdemo.config;

import cn.yoyo.redisdemo.web.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class MySpringmvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 静态方法放行
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 拦截器配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new AuthInterceptor(stringRedisTemplate));
        registration.addPathPatterns("/profile.html","/home.html");                      //拦截的路径
    }
}
