package com.material.manaement.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName("biz_categories")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private String icon;

    @TableField("sort_order")
    private Integer sort;

    private Date createTime;
    private Integer isDeleted;

}
