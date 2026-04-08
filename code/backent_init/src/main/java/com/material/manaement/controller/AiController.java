package com.material.manaement.controller;

import com.material.manaement.common.Result;
import com.material.manaement.tool.ProjectSummaryTool;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final ProjectSummaryTool projectSummaryTool;

    /**
     * 生成项目摘要描述
     * 
     * @param requestBody 包含 content 的 JSON 数据
     * @return 智能生成的摘要
     */
    @PostMapping("/generate-summary")
    public Result<String> generateSummary(@RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.failed("待处理内容不能为空");
        }
        try {
            String summary = projectSummaryTool.generateProjectSummary(content);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.failed("AI 业务工具执行失败：" + e.getMessage());
        }
    }
}
