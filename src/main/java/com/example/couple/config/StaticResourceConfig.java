package com.example.couple.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadProperties.dir())
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        String mediaUrl = uploadProperties.mediaUrl();
        registry.addResourceHandler(mediaUrl + "**")
                .addResourceLocations(uploadPath);
    }
}