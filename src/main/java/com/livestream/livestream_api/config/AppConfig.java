package com.livestream.livestream_api.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;


import java.io.IOException;
import java.nio.file.*;


@Configuration
public class AppConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public String uploadDirectory() throws IOException {
        var path = Paths.get(uploadDir);
        if (!Files.exists(path)) Files.createDirectories(path);
        return uploadDir;
    }
}