package com.material.manaement.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String upload(MultipartFile file, String bucketName);

    void delete(String objectName, String bucketName);
}
