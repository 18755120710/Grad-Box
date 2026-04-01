package com.material.manaement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.manaement.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
