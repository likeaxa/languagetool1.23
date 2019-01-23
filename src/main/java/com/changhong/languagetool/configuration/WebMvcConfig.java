package com.changhong.languagetool.configuration;

import com.changhong.languagetool.handler.UserHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 添加静态资源文件，外部可以直接访问地址
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserHandler())
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/File")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/fileupload")
                .excludePathPatterns("/getcountry")
                .excludePathPatterns("/worldtranstion/**")
                .excludePathPatterns("/updatefile")
                .excludePathPatterns("/download2/**")
                .excludePathPatterns("/user");
    }
}
