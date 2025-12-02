package com.reactpf.reactpf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry){
        
        registry.addResourceHandler("/imagePath/**")
                .addResourceLocations("file:///C:/Users/must1/Desktop/temp/");
    
        registry.addResourceHandler("/imagePath/posts/**")
                .addResourceLocations("file:///C:/Users/must1/Desktop/communityImg/");
    
    }

    

}
