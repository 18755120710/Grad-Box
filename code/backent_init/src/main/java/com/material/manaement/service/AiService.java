package com.material.manaement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;

    /**
     * 通用的 AI 调用底层方法
     * 提供统一的错误处理和内容提取逻辑
     * 
     * @param prompt 构造好的 Prompt 对象
     * @return AI 返回的纯文本内容
     */
    public String call(Prompt prompt) {
        try {
            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (Exception e) {
            // 这里可以统一记录日志或进行重试逻辑封装
            throw new RuntimeException("AI 调用基座异常: " + e.getMessage());
        }
    }
}
