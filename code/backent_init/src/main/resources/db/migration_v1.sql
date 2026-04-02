-- ----------------------------
-- Structured technical details
-- ----------------------------
CREATE TABLE IF NOT EXISTS `biz_project_tech_details` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `group_name` varchar(100) NOT NULL COMMENT '分组名称 (如: 后端架构, 前端技术)',
  `item_name` varchar(100) NOT NULL COMMENT '技术项 (如: Spring Boot, Vue 3)',
  `sort_order` int DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目技术细节表';

-- ----------------------------
-- Project visits for visitor dynamics
-- ----------------------------
CREATE TABLE IF NOT EXISTS `biz_project_visits` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), 
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目访问记录表';

-- ----------------------------
-- Update consultations status and priority
-- ----------------------------
SET @dbname = DATABASE();
SET @tablename = 'mkt_consultations';
SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  'ALTER TABLE `mkt_consultations` ADD COLUMN `status` tinyint DEFAULT 0 COMMENT \'状态: 0-待回复, 1-已回复, 2-已隐藏\''
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @columnname = 'priority';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @columnname) > 0,
  'SELECT 1',
  'ALTER TABLE `mkt_consultations` ADD COLUMN `priority` tinyint DEFAULT 0 COMMENT \'优先级: 0-普通, 1-紧急\''
));
PREPARE stmt FROM @preparedStatement;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- Add Home Page Config keys
-- ----------------------------
INSERT INTO `sys_configs` (`config_key`, `config_value`, `config_desc`, `is_system`) 
VALUES 
('home_title', '成就卓越 毕业作品', '首页主标题', 1),
('home_subtitle', '汇集顶级毕业课题，涵盖高质源码与全栈思维指导。每一份交付，都是对技术灵魂的深度刻画。', '首页副标题', 1),
('home_banner_url', '', '首页 Banner 资源 URL', 1),
('home_recommend_ids', '1,2,3', '首页推荐项目 ID 列表 (逗号分隔)', 1)
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`);
