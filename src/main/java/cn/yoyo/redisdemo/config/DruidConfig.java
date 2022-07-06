package cn.yoyo.redisdemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PropertySource(value = "classpath:druid.properties")
@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
        return new DruidDataSource();
    }

    //配置druid监控
    //1、配置管理后台的servler
    @Bean
    public ServletRegistrationBean setViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet());
        bean.setUrlMappings(Arrays.asList(new String[]{"/druid/*"}));
        Map<String,String> initParams = new HashMap<>();
        initParams.put("loginUsername","admin"); //登录后台的用户名
        initParams.put("loginPassword","123456"); //登录后台的密码
        initParams.put("allow",""); // 空字符串就是允许所有地址访问，生产环境可以指定语句ip地址
        //initParams.put("deny",""); // 可以指定拒绝某个id地址的访问，这里就不设置了
        bean.setInitParameters(initParams);
        return bean;
    }

    //配置web监控的filter
    @Bean
    public FilterRegistrationBean setFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        bean.setUrlPatterns(Arrays.asList(new String[]{"/*"}));
        //有些东西是不能拦截的，所以要配置exclusions排除掉
        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,*.jpg,*.png,/druid/*");//这些是不能拦截的
        bean.setInitParameters(initParams);
        return bean;
    }

}
