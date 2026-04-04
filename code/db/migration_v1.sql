-- PRD V1 Database Migration Script
-- ------------------------------------------------------------

-- 1. 项目技术细节表 (P0)
CREATE TABLE `biz_project_tech_details` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `group_name` varchar(50) NOT NULL COMMENT '分组名称 (如: 后端, 前端, 数据库)',
  `item_name` varchar(100) NOT NULL COMMENT '技术项名称 (如: Spring Boot 3.3)',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目技术细节展现';

-- 2. 项目访问统计/动态表 (P0)
CREATE TABLE `biz_project_visits` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '访问者ID',
  `visit_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目访客记录';

-- 3. 优化咨询表 (P0)
ALTER TABLE `mkt_consultations` ADD COLUMN `status` tinyint DEFAULT 0 COMMENT '状态: 0-待回复, 1-已回复, 2-已隐藏';
ALTER TABLE `mkt_consultations` ADD COLUMN `priority` tinyint DEFAULT 0 COMMENT '优先级: 0-普通, 1-紧急';

-- 4. 首页配置表 (P0)
CREATE TABLE `sys_configs` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL UNIQUE,
  `config_value` text,
  `description` varchar(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局系统配置';

INSERT INTO `sys_configs` (`config_key`, `config_value`, `description`) VALUES
('home_title', '成就卓越 毕业作品', '首页主标题'),
('home_subtitle', '汇集顶级毕业课题，涵盖高质源码与全栈思维指导。每一份交付，都是对技术灵魂的深度刻画。', '首页副标题'),
('home_recommend_ids', '1,2,3', '首页推荐项目集 (逗号分隔)');

-- 5. 用户活动日志表 (P1)
CREATE TABLE `sys_user_logs` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `action` varchar(100) NOT NULL,
  `details` text,
  `ip` varchar(50),
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为审计日志';
