package com.example.project.config;

import com.example.project.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;


@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //放行哪些原始域
                .allowedOrigins("http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
     * 配置静态资源的获取
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:C:/Users/86130/Desktop/project/java/file/img/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    /**
     * 配置无token用户的拦截接口
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/common/uploadImg")
                .addPathPatterns("/release/startModify")
                .addPathPatterns("/release/version/add")
                .addPathPatterns("/release/version/update")
                .addPathPatterns("/release/mission")
                .addPathPatterns("/release/audit")
                .addPathPatterns("/release/update")
                .addPathPatterns("/release/add")
                .addPathPatterns("/release/main/add")
                .addPathPatterns("/artist/startModify")
                .addPathPatterns("/artist/version/add")
                .addPathPatterns("/artist/mission")
                .addPathPatterns("/artist/audit")
                .addPathPatterns("/label/startModify")
                .addPathPatterns("/label/version/add")
                .addPathPatterns("/label/mission")
                .addPathPatterns("/label/audit")
                .addPathPatterns("/style/startModify")
                .addPathPatterns("/style/version/add")
                .addPathPatterns("/style/mission")
                .addPathPatterns("/style/audit")
                .addPathPatterns("/user/audit/verify");
//                .addPathPatterns("/user/contribute/all");

//                .excludePathPatterns("/login/**")
//                .excludePathPatterns("/register/**")
//                .excludePathPatterns("/**");

        WebMvcConfigurer.super.addInterceptors(registry);
    }



}
