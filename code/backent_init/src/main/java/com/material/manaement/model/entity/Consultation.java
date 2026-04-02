package com.material.manaement.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName("mkt_consultations")
public class Consultation implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long projectId;
    private String content;
    private String contactWay;

    @TableField("is_processed")
    private Integer status;

    @TableField("admin_reply")
    private String reply;

    private Date replyTime;
    private Integer priority;
    @TableField(exist = false)
    private String projectTitle;
    private Date createTime;
}
