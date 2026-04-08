package com.material.manaement.controller;

import com.material.manaement.common.Result;
import com.material.manaement.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

            // 确保存储桶存在并设置为公开读取
            checkAndCreateBucket();

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

    @Operation(summary = "检查分片状态（断点续传）")
    @GetMapping("/upload/check")
    public com.material.manaement.common.Result<Map<String, Object>> checkChunkUpload(
            @RequestParam("identifier") String identifier) {
        try {
            String prefix = "chunks/" + identifier + "/";
            Iterable<io.minio.Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .prefix(prefix)
                            .recursive(true)
                            .build());

            List<Integer> uploadedChunks = new ArrayList<>();
            for (io.minio.Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                // objectName 格式为 chunks/{identifier}/{index}
                String indexStr = objectName.substring(objectName.lastIndexOf("/") + 1);
                try {
                    uploadedChunks.add(Integer.parseInt(indexStr));
                } catch (NumberFormatException ignored) {
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("uploadedChunks", uploadedChunks);
            // uploadId 直接复用 identifier，简化逻辑
            data.put("uploadId", identifier);
            return com.material.manaement.common.Result.success(data);
        } catch (Exception e) {
            log.error("检查分片异常: ", e);
            return com.material.manaement.common.Result.failed("分片检查失败");
        }
    }

    @Operation(summary = "取消上传并清理分片")
    @PostMapping("/upload/cleanup")
    public com.material.manaement.common.Result<String> cleanupChunks(
            @RequestParam("uploadId") String uploadId) {
        try {
            String prefix = "chunks/" + uploadId + "/";
            Iterable<io.minio.Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .prefix(prefix)
                            .recursive(true)
                            .build());

            List<DeleteObject> objects = new ArrayList<>();
            for (io.minio.Result<Item> result : results) {
                objects.add(new DeleteObject(result.get().objectName()));
            }

            if (!objects.isEmpty()) {
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(minioConfig.getBucket())
                                .objects(objects)
                                .build());
            }

            return com.material.manaement.common.Result.success("中间分片已清理");
        } catch (Exception e) {
            log.error("清理分片异常: ", e);
            return com.material.manaement.common.Result.failed("清理过程出现异常");
        }
    }

    @Operation(summary = "初始化分片上传")
    @PostMapping("/upload/init")
    public com.material.manaement.common.Result<Map<String, Object>> initChunkUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "identifier", required = false) String identifier) {
        // 如果提供了 identifier (MD5)，则优先使用它作为 uploadId 以便续传识别
        String uploadId = (identifier != null && !identifier.isEmpty()) ? identifier : UUID.randomUUID().toString();
        String extension = "";
        if (fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("uploadId", uploadId);
        data.put("extension", extension);
        return com.material.manaement.common.Result.success(data);
    }

    @Operation(summary = "上传分片")
    @PostMapping("/upload/chunk")
    public Result<String> uploadChunk(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("index") Integer index,
            @RequestParam("file") MultipartFile file) {
        try {
            String objectName = "chunks/" + uploadId + "/" + index;
            // 确保桶存在
            checkAndCreateBucket();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType("application/octet-stream")
                            .build());
            return Result.success("分片 " + index + " 上传成功");
        } catch (Exception e) {
            log.error("分片上传异常: ", e);
            return Result.failed("分片上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "完成分片上传")
    @PostMapping("/upload/complete")
    public Result<String> completeChunkUpload(
            @RequestParam("uploadId") String uploadId,
            @RequestParam("fileName") String fileName,
            @RequestParam("totalChunks") Integer totalChunks) {
        try {
            String extension = "";
            if (fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }

            // 目录分配逻辑优化
            String folder = "others/";
            String ext = extension.toLowerCase();
            if (Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(ext)) {
                folder = "images/";
            } else if (Arrays.asList(".mp4", ".mov", ".avi", ".mkv").contains(ext)) {
                folder = "videos/";
            }

            String finalObjectName = folder + UUID.randomUUID() + extension;

            List<ComposeSource> sources = new ArrayList<>();
            for (int i = 0; i < totalChunks; i++) {
                sources.add(ComposeSource.builder()
                        .bucket(minioConfig.getBucket())
                        .object("chunks/" + uploadId + "/" + i)
                        .build());
            }

            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(finalObjectName)
                            .sources(sources)
                            .build());

            // 异步清理分片可以提升响应速度，但这里同步清理以保证一致性
            for (int i = 0; i < totalChunks; i++) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioConfig.getBucket())
                                .object("chunks/" + uploadId + "/" + i)
                                .build());
            }

            String fileUrl = minioConfig.getPublicUrl() + "/" + finalObjectName;
            log.info("大文件分片合并成功: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("合并分片异常: ", e);
            return Result.failed("合并失败: " + e.getMessage());
        }
    }

    private void checkAndCreateBucket() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
        }

        // 强制设置公开读取策略
        String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::"
                + minioConfig.getBucket()
                + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::"
                + minioConfig.getBucket() + "/*\"]}]}";
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder().bucket(minioConfig.getBucket()).config(policy).build());
    }

    @Operation(summary = "通过 MinIO 删除文件")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam String url) {
        try {
            // 从 URL 中提取对象名称
            // URL 格式: http://ip:port/bucketName/path/to/file
            String bucketName = minioConfig.getBucket();
            String objectName = url.substring(url.indexOf(bucketName) + bucketName.length() + 1);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return Result.success("文件已从云端移除");
        } catch (Exception e) {
            log.error("MinIO 删除异常: ", e);
            return Result.failed("文件删除失败: " + e.getMessage());
        }
    }
}
