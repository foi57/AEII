package org.demo.artExaminationInformationInquiry.config.WebMvcConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/university/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/file/university/");
        registry.addResourceHandler("/article/images/**")
               .addResourceLocations("file:" + System.getProperty("user.dir") + "/file/article/images/");
        // 修改为实际存储路径
    }


}