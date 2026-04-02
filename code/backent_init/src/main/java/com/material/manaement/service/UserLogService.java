package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.UserLog;

public interface UserLogService extends IService<UserLog> {
    void log(Long userId, String action, String details, String ip);
}
