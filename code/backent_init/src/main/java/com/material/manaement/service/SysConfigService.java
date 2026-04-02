package com.material.manaement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.material.manaement.model.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {
    String getValue(String key);

    void setValue(String key, String value);
}
