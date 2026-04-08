package com.material.manaement.tool;

import com.material.manaement.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 项目智能助手工具集
 * 包含所有可供 AI Agent 调用处理项目资产的工具方法
 */
@Component
@RequiredArgsConstructor
public class ProjectSummaryTool {


    private final AiService aiService;

    /**
     * 生成项目摘要描述
     * 
     * @param content 项目深度解析或 PRD 的 Markdown 内容
     * @return AI 提炼后的 100 字左右摘要
     */
    @Tool(description = "分析项目的深度解析或需求文档，并为其生成一个简洁、专业的摘要描述")
    public String generateProjectSummary(String content) {
        String userPrompt = """
                你是一个专业的项目经理。请分析以下项目的深度解析（Markdown 格式），并为其生成一个简洁的摘要描述。
                要求：
                1. 长度在 80-120 字左右。
                2. 突出项目的核心功能和技术亮点。
                3. 用于展示卡片的简要介绍，语言要具有吸引力。
                4. 直接输出摘要内容，不要有任何前缀或解释。

                项目内容如下：
                {content}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
        Prompt prompt = promptTemplate.create(Map.of("content", content));

        return aiService.call(prompt);
    }
}
