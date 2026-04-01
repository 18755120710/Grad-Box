package com.material.manaement.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName("trade_orders")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long projectId;
    private Double totalAmount;
    private Double payAmount;
    private Integer paymentMethod;
    private Integer status;
    private Date payTime;
    private Date deliveryTime;
    private String deliveryContent;
    private String userRemark;
    private String adminRemark;
    private Date createTime;
    private Date updateTime;
}
