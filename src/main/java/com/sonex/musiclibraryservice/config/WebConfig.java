package com.sonex.musiclibraryservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path /uploads/** to your local folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:E:/Sonex/Software Development/photos/uploads/");
    }
}
