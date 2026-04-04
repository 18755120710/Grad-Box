package com.material.manaement.controller;

import com.material.manaement.common.Result;
import com.material.manaement.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "MinIO 文件存储服务")
public class FileController {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Operation(summary = "通过 MinIO 上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.failed("上传文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 分类逻辑
            String folder = "others/";
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    folder = "images/";
                } else if (contentType.startsWith("video/")) {
                    folder = "videos/";
                }
            }

            String objectName = folder + UUID.randomUUID() + extension;

            // 确保存储桶存在
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType)
                            .build());

            String fileUrl = minioConfig.getPublicUrl() + "/" + objectName;
            log.info("文件上传成功: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("MinIO 上传异常: ", e);
            return Result.failed("上传失败: " + e.getMessage());
        }
    }
}
