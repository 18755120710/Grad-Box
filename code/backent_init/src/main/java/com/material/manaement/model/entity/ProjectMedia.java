package com.material.manaement.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("biz_project_medias")
public class ProjectMedia implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String mediaUrl;

    /**
     * 1-图片, 2-视频演示
     */
    private Integer mediaType;

    private Integer sortOrder;
}
