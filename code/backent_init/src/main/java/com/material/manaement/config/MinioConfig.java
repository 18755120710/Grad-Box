package com.material.manaement.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://47.102.205.85:19001")
                .credentials("minio", "wj08265395")
                .build();
    }
}
