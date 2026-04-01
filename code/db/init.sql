-- 毕业设计项目展示与服务平台 - 资深架构版数据库设计 (MySQL 8.0+)
-- 核心目标：模块化、可扩展、支持审计与多场景服务

CREATE DATABASE IF NOT EXISTS `grad_box_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `grad_box_db`;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 权限与安全模块 (RBAC)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_users` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `referral_code` VARCHAR(20) DEFAULT NULL UNIQUE COMMENT '个人推荐码',
    `referrer_id` BIGINT DEFAULT NULL COMMENT '推荐人ID',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB COMMENT='系统用户表';

-- ----------------------------
-- 2. 内容管理模块 (CMS/Projects)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `biz_categories` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `level` TINYINT DEFAULT 1 COMMENT '层级',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB COMMENT='项目分类表';

CREATE TABLE IF NOT EXISTS `biz_projects` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL COMMENT '项目名称',
    `brief` VARCHAR(500) DEFAULT NULL COMMENT '一句话卖点',
    `content_html` LONGTEXT DEFAULT NULL COMMENT '详细介绍(HTML)',
    `category_id` BIGINT NOT NULL COMMENT '所属分类',
    `tech_stacks` VARCHAR(500) DEFAULT NULL COMMENT '技术标签 (如: Spring Boot,Vue,Python)',
    `difficulty` TINYINT DEFAULT 1 COMMENT '难易程度: 1-简单, 2-中等, 3-复杂',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '主封面',
    `price` DECIMAL(10, 2) NOT NULL DEFAULT 49.00 COMMENT '参考包价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价/划线价',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否精选',
    `is_new` TINYINT DEFAULT 0 COMMENT '是否新品',
    `sort_order` INT DEFAULT 0,
    `view_count` INT DEFAULT 0,
    `sale_count` INT DEFAULT 0,
    `fav_count` INT DEFAULT 0 COMMENT '收藏数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB COMMENT='项目表';

CREATE TABLE IF NOT EXISTS `biz_project_medias` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `project_id` BIGINT NOT NULL,
    `media_url` VARCHAR(255) NOT NULL,
    `media_type` TINYINT DEFAULT 1 COMMENT '类型: 1-图片, 2-视频演示',
    `sort_order` INT DEFAULT 0
) ENGINE=InnoDB COMMENT='项目媒体库';

-- ----------------------------
-- 3. 交易与订单模块
-- ----------------------------
CREATE TABLE IF NOT EXISTS `trade_orders` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` BIGINT NOT NULL,
    `project_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总额',
    `pay_amount` DECIMAL(10, 2) DEFAULT 0 COMMENT '实付金额',
    `payment_method` TINYINT DEFAULT 0 COMMENT '支付方式: 0-未选择, 1-微信, 2-支付宝, 3-余额, 4-人工转账',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已交付(参考包), 3-已完成, 4-已取消, 5-退款中',
    `pay_time` DATETIME DEFAULT NULL,
    `delivery_time` DATETIME DEFAULT NULL,
    `delivery_content` TEXT DEFAULT NULL COMMENT '交付文件下载URL或提取码',
    `user_remark` VARCHAR(500) DEFAULT NULL,
    `admin_remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS `trade_payment_records` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(64) NOT NULL,
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '三方交易流水号',
    `amount` DECIMAL(10, 2) NOT NULL,
    `pay_status` TINYINT DEFAULT 0 COMMENT '0-处理中, 1-成功, 2-失败',
    `raw_response` TEXT DEFAULT NULL COMMENT '三方接口原始响应',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='支付记录审计表';

-- ----------------------------
-- 4. 服务工单模块 (增值服务)
-- ----------------------------
CREATE TABLE IF NOT EXISTS `svc_work_orders` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `work_no` VARCHAR(64) NOT NULL UNIQUE COMMENT '工单号',
    `parent_order_id` BIGINT NOT NULL COMMENT '关联的主订单',
    `user_id` BIGINT NOT NULL,
    `type` TINYINT NOT NULL COMMENT '1-个性化修改, 2-远程部署, 3-演示视频录制',
    `title` VARCHAR(255) NOT NULL,
    `requirement` TEXT NOT NULL COMMENT '详细需求描述',
    `attachments` TEXT DEFAULT NULL COMMENT '附件JSON',
    `platform_quote` DECIMAL(10, 2) DEFAULT 0 COMMENT '客服给出的报价',
    `status` TINYINT DEFAULT 0 COMMENT '0-待沟通, 1-待报价, 2-待支付, 3-开发/部署中, 4-待验收, 5-已完成',
    `assignee_id` BIGINT DEFAULT NULL COMMENT '分配的技术负责人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='增值服务工单';

-- ----------------------------
-- 5. 互动与营销模块
-- ----------------------------
CREATE TABLE IF NOT EXISTS `mkt_consultations` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT DEFAULT NULL,
    `project_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `contact_way` VARCHAR(100) NOT NULL,
    `is_processed` TINYINT DEFAULT 0,
    `admin_reply` TEXT DEFAULT NULL,
    `reply_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='咨询留言表';

CREATE TABLE IF NOT EXISTS `mkt_reviews` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `project_id` BIGINT NOT NULL,
    `order_id` BIGINT NOT NULL,
    `rating` TINYINT DEFAULT 5 COMMENT '评分1-5',
    `comment` TEXT DEFAULT NULL,
    `images` TEXT DEFAULT NULL COMMENT '评价晒图JSON',
    `is_show` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='项目评价表';

CREATE TABLE IF NOT EXISTS `mkt_referral_log` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `referrer_id` BIGINT NOT NULL COMMENT '推荐人ID',
    `referee_id` BIGINT NOT NULL COMMENT '被推荐人ID',
    `order_id` BIGINT NOT NULL,
    `commission_amount` DECIMAL(10, 2) NOT NULL,
    `status` TINYINT DEFAULT 0 COMMENT '0-冻结中, 1-已入账, 2-已失效',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='推广分佣日志';

-- ----------------------------
-- 6. 系统配置与日志模块
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_configs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `config_key` VARCHAR(50) NOT NULL UNIQUE,
    `config_value` TEXT DEFAULT NULL,
    `config_desc` VARCHAR(255) DEFAULT NULL,
    `is_system` TINYINT DEFAULT 0 COMMENT '是否系统内置'
) ENGINE=InnoDB COMMENT='系统全局配置表';

CREATE TABLE IF NOT EXISTS `sys_operation_logs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `operator_id` BIGINT DEFAULT NULL,
    `module` VARCHAR(50) DEFAULT NULL,
    `action` VARCHAR(100) DEFAULT NULL,
    `ip` VARCHAR(50) DEFAULT NULL,
    `params` TEXT DEFAULT NULL,
    `result` TEXT DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='系统操作日志';

SET FOREIGN_KEY_CHECKS = 1;
