package com.material.manaement.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@TableName("biz_projects")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;

    @TableField("brief")
    private String description;

    private String contentHtml;
    private String coverImage;
    private String demoUrl;
    private Long categoryId;
    private Double price;
    private Double originalPrice;

    @TableField("tech_stacks")
    private String techStack;

    private Integer difficulty;
    private Integer downloadCount;
    private Integer viewCount;
    private Integer saleCount;
    private Integer favCount;
    private Integer status;
    private Integer isRecommend;
    private Integer isNew;
    private String tags;
    private String major;
    private Integer sortOrder;
    @TableField(exist = false)
    private java.util.List<ProjectMedia> medias;

    @TableField(exist = false)
    private List<ProjectTechDetail> techDetails;

    @TableField(exist = false)
    private List<User> recentVisitors;

    @TableField(exist = false)
    private String categoryName;

    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

}
