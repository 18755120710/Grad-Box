package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.UserLogMapper;
import com.material.manaement.model.entity.UserLog;
import com.material.manaement.service.UserLogService;
import org.springframework.stereotype.Service;

@Service
public class UserLogServiceImpl extends ServiceImpl<UserLogMapper, UserLog> implements UserLogService {
    @Override
    public void log(Long userId, String action, String details, String ip) {
        UserLog log = new UserLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setIp(ip);
        this.save(log);
    }
}
