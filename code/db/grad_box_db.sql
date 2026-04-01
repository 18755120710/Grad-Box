/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : 127.0.0.1:3306
 Source Schema         : grad_box_db

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 31/03/2026 15:56:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_categories
-- ----------------------------
DROP TABLE IF EXISTS `biz_categories`;
CREATE TABLE `biz_categories`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `level` tinyint NULL DEFAULT 1 COMMENT '层级',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_categories
-- ----------------------------
INSERT INTO `biz_categories` VALUES (1, 'Java后端', 0, 1, 1, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (2, 'Python/AI', 0, 1, 2, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (3, '微信小程序', 0, 1, 3, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (4, '大数据分析', 0, 1, 4, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (5, '游戏开发', 0, 1, 5, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (6, '硬件/IoT', 0, 1, 6, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (7, '前端技术', 0, 1, 7, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (8, '区块链技术', 0, 1, 8, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (9, '网络安全', 0, 1, 9, NULL, '2026-03-31 03:31:04', 0);
INSERT INTO `biz_categories` VALUES (10, '其他综合', 0, 1, 10, NULL, '2026-03-31 03:31:04', 0);

-- ----------------------------
-- Table structure for biz_project_medias
-- ----------------------------
DROP TABLE IF EXISTS `biz_project_medias`;
CREATE TABLE `biz_project_medias`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint NOT NULL,
  `media_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `media_type` tinyint NULL DEFAULT 1 COMMENT '类型: 1-图片, 2-视频演示',
  `sort_order` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目媒体库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_project_medias
-- ----------------------------

-- ----------------------------
-- Table structure for biz_projects
-- ----------------------------
DROP TABLE IF EXISTS `biz_projects`;
CREATE TABLE `biz_projects`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `brief` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '一句话卖点',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详细介绍(HTML)',
  `category_id` bigint NOT NULL COMMENT '所属分类',
  `tech_stacks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技术标签 (如: Spring Boot,Vue,Python)',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难易程度: 1-简单, 2-中等, 3-复杂',
  `download_count` int NULL DEFAULT 0,
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主封面',
  `demo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL DEFAULT 49.00 COMMENT '参考包价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价/划线价',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
  `is_recommend` tinyint NULL DEFAULT 0 COMMENT '是否精选',
  `is_new` tinyint NULL DEFAULT 0 COMMENT '是否新品',
  `sort_order` int NULL DEFAULT 0,
  `view_count` int NULL DEFAULT 0,
  `sale_count` int NULL DEFAULT 0,
  `fav_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_projects
-- ----------------------------
INSERT INTO `biz_projects` VALUES (1, '基于 Spring Cloud 的微服务养老商城', '高性能微服务养老物资购物平台，支持高并发与分布式事务。', '<p>详细介绍：Nacos + Gateway + Sentinel 架构...</p>', 1, 'Java,Spring Cloud,Vue 3', 3, 0, 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?q=80&w=2026&auto=format&fit=crop', NULL, 299.00, NULL, 1, 1, 0, 0, 1200, 45, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (2, '智能手势识别与无人机控制系统', '利用 MediaPipe 与 OpenCV 实现高精度的手势语义解析。', '<p>基于 Python 的深度学习识别引擎...</p>', 2, 'Python,OpenCV,Flask', 3, 0, 'https://images.unsplash.com/photo-1508997449629-303059a1b1c0?q=80&w=2070&auto=format&fit=crop', NULL, 499.00, NULL, 1, 1, 0, 0, 890, 22, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (3, '智慧校园微信小程序全栈系统', '涵盖课表查询、失物招领等功能的 Uni-app 校园全能助手。', '<p>完美的微信小程序用户交互体验...</p>', 3, 'Uni-app,Node.js,MySQL', 2, 0, 'https://images.unsplash.com/photo-1546410531-bb4caa1b424d?q=80&w=2071&auto=format&fit=crop', NULL, 199.00, NULL, 1, 1, 0, 0, 5600, 120, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (4, '电商平台用户行为大数据分析', '基于 Hadoop + Spark 抽取海量用户点击流数据。', '<p>包含实时处理与离线分析模块...</p>', 4, 'Hadoop,Spark,Kafka', 3, 0, 'https://images.unsplash.com/photo-1551288049-bbda4e18f7ad?q=80&w=2070&auto=format&fit=crop', NULL, 350.00, NULL, 1, 0, 0, 0, 1100, 31, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (5, '虚幻引擎5 开放世界 RPG 游戏', '使用 UE5 开发的 3D RPG，包含技能系统与 AI 行为树。', '<p>顶级视觉表现与物理交互...</p>', 5, 'C++,UE5', 3, 0, 'https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop', NULL, 599.00, NULL, 1, 1, 0, 0, 450, 12, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (6, '基于 ESP32 的智能家居监控中心', '包含温湿度采集与云端联动，支持 Web 端实时控制。', '<p>低成本、高可靠的硬件控制方案...</p>', 6, 'ESP32,Arduino,Vue', 2, 0, 'https://images.unsplash.com/photo-1558346490-a72e53ae2d4f?q=80&w=2070&auto=format&fit=crop', NULL, 150.00, NULL, 1, 0, 0, 0, 780, 56, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (7, 'React 高性能在线协作 Markdown 编辑器', '支持多人同时在线编辑（Yjs）与思维导图转换。', '<p>纯正的 React Hooks 开发模式...</p>', 7, 'React,Node.js,Yjs', 2, 0, 'https://images.unsplash.com/photo-1522252234503-e356532cafd5?q=80&w=2025&auto=format&fit=crop', NULL, 120.00, NULL, 1, 0, 0, 0, 670, 44, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (8, '基于 Go 语言的高并发秒杀系统', '利用 Redis 缓存预热与消息队列异步下单。', '<p>极致的性能优化与限流策略...</p>', 1, 'Golang,Redis,RabbitMQ', 3, 0, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=2072&auto=format&fit=crop', NULL, 380.00, NULL, 1, 0, 0, 0, 450, 28, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (9, 'Web 安全漏洞扫描与防御展示平台', '模拟 SQL 注入、XSS 攻击并实时展示防御策略。', '<p>集成 Nmap 扫描与 WAF 防护演示...</p>', 9, 'Python,Django,Scapy', 3, 0, 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=2070&auto=format&fit=crop', NULL, 260.00, NULL, 1, 1, 0, 0, 340, 15, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `biz_projects` VALUES (10, '基于区块链的学历证书认证系统', '利用以太坊智能合约实现学历证书上链真伪验证。', '<p>去中心化的数据存储与验证逻辑...</p>', 8, 'Solidity,Web3.js', 3, 0, 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?q=80&w=2064&auto=format&fit=crop', NULL, 480.00, NULL, 1, 1, 0, 0, 990, 33, 0, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);

-- ----------------------------
-- Table structure for mkt_consultations
-- ----------------------------
DROP TABLE IF EXISTS `mkt_consultations`;
CREATE TABLE `mkt_consultations`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `project_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_way` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_processed` tinyint NULL DEFAULT 0,
  `admin_reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `reply_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '咨询留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mkt_consultations
-- ----------------------------
INSERT INTO `mkt_consultations` VALUES (1, 2, 1, '可以提供源码的详细架构图吗？', '微信: wx_student01', 1, '已在资料包中包含。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (2, 3, 2, '这个系统支持 NVIDIA 以外的显卡吗？', '手机: 138XXXX1111', 1, '目前仅适配了 CUDA 加速，AMD 显卡需要修改识别后端。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (3, 4, 3, 'Uni-app 版本是多少？', 'QQ: 123456', 0, NULL, NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (4, 5, 4, '虚拟机的最低配置要求是多少？', '微信: wx_bigdata', 1, '建议 16G 内存。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (5, 6, 5, '支持多人联机吗？', '手机: 138XXXX5555', 0, NULL, NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (6, 7, 6, '是否包含 PCB 原理图？', '微信: iot_expert', 1, '包含立创 EDA 的原理图文件。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (7, 8, 7, '可以集成到 WordPress 中吗？', 'QQ: 778899', 0, NULL, NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (8, 9, 8, '秒杀逻辑是用 Lua 脚本实现的吗？', '微信: go_coder', 1, '是的，利用 Lua 脚本保证 Redis 操作的原子性。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (9, 2, 9, '有防御报告模版吗？', '手机: 138XXXX9999', 1, '资料包内含一份 WAF 防护效果对比模板。', NULL, '2026-03-31 03:31:04');
INSERT INTO `mkt_consultations` VALUES (10, 4, 10, '智能合约已经部署在 Rinkeby 测试网了吗？', '微信: chain_student', 0, NULL, NULL, '2026-03-31 03:31:04');

-- ----------------------------
-- Table structure for mkt_referral_log
-- ----------------------------
DROP TABLE IF EXISTS `mkt_referral_log`;
CREATE TABLE `mkt_referral_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `referrer_id` bigint NOT NULL COMMENT '推荐人ID',
  `referee_id` bigint NOT NULL COMMENT '被推荐人ID',
  `order_id` bigint NOT NULL,
  `commission_amount` decimal(10, 2) NOT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '0-冻结中, 1-已入账, 2-已失效',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '推广分佣日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mkt_referral_log
-- ----------------------------

-- ----------------------------
-- Table structure for mkt_reviews
-- ----------------------------
DROP TABLE IF EXISTS `mkt_reviews`;
CREATE TABLE `mkt_reviews`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `project_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `rating` tinyint NULL DEFAULT 5 COMMENT '评分1-5',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价晒图JSON',
  `is_show` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mkt_reviews
-- ----------------------------

-- ----------------------------
-- Table structure for svc_work_orders
-- ----------------------------
DROP TABLE IF EXISTS `svc_work_orders`;
CREATE TABLE `svc_work_orders`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `work_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单号',
  `parent_order_id` bigint NOT NULL COMMENT '关联的主订单',
  `user_id` bigint NOT NULL,
  `type` tinyint NOT NULL COMMENT '1-个性化修改, 2-远程部署, 3-演示视频录制',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细需求描述',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '附件JSON',
  `platform_quote` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '客服给出的报价',
  `status` tinyint NULL DEFAULT 0 COMMENT '0-待沟通, 1-待报价, 2-待支付, 3-开发/部署中, 4-待验收, 5-已完成',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '分配的技术负责人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_no`(`work_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '增值服务工单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of svc_work_orders
-- ----------------------------

-- ----------------------------
-- Table structure for sys_configs
-- ----------------------------
DROP TABLE IF EXISTS `sys_configs`;
CREATE TABLE `sys_configs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `config_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_system` tinyint NULL DEFAULT 0 COMMENT '是否系统内置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统全局配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_configs
-- ----------------------------

-- ----------------------------
-- Table structure for sys_operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_logs`;
CREATE TABLE `sys_operation_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NULL DEFAULT NULL,
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_logs
-- ----------------------------

-- ----------------------------
-- Table structure for sys_users
-- ----------------------------
DROP TABLE IF EXISTS `sys_users`;
CREATE TABLE `sys_users`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'user',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `referral_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人推荐码',
  `referrer_id` bigint NULL DEFAULT NULL COMMENT '推荐人ID',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `referral_code`(`referral_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_users
-- ----------------------------
INSERT INTO `sys_users` VALUES (1, 'admin', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '管理员-陈学长', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 'admin@gradbox.com', '13800000001', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (2, 'user01', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '张三', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user01', 'user01@example.com', '13811111111', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (3, 'user02', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '李四', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user02', 'user02@example.com', '13822222222', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (4, 'user03', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '王五', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user03', 'user03@example.com', '13833333333', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (5, 'user04', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '赵六', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user04', 'user04@example.com', '13844444444', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (6, 'user05', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '孙七', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user05', 'user05@example.com', '13855555555', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (7, 'user06', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '周八', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user06', 'user06@example.com', '13866666666', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (8, 'user07', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '吴九', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user07', 'user07@example.com', '13877777777', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (9, 'user08', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '郑十', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user08', 'user08@example.com', '13888888888', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (10, 'test01', '$2a$10$86q5j/g2e7w9.k2y.8r1.u1z4z/0z.w5r1/y', '测试员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=test01', 'test01@example.com', '13899999999', 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04', 0);
INSERT INTO `sys_users` VALUES (12, 'butvan', '$2a$10$4BzJHdoa9lo2exbf6YOo5eG3mXKlNRdoeGutE1co93kzn3BPOND32', '可梵', NULL, NULL, NULL, 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 15:28:19', '2026-03-31 07:28:19', 0);
INSERT INTO `sys_users` VALUES (13, 'tester', '$2a$10$v.NUVXohnmO8loCKyzzXI.xoWOiKViGJG07mybvzerdY48ueEhNOq', 'tester', NULL, NULL, NULL, 'user', 1, NULL, NULL, NULL, NULL, '2026-03-31 15:33:15', '2026-03-31 07:33:15', 0);

-- ----------------------------
-- Table structure for trade_orders
-- ----------------------------
DROP TABLE IF EXISTS `trade_orders`;
CREATE TABLE `trade_orders`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL,
  `project_id` bigint NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总额',
  `pay_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实付金额',
  `payment_method` tinyint NULL DEFAULT 0 COMMENT '支付方式: 0-未选择, 1-微信, 2-支付宝, 3-余额, 4-人工转账',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已交付(参考包), 3-已完成, 4-已取消, 5-退款中',
  `pay_time` datetime NULL DEFAULT NULL,
  `delivery_time` datetime NULL DEFAULT NULL,
  `delivery_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '交付文件下载URL或提取码',
  `user_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `admin_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_orders
-- ----------------------------
INSERT INTO `trade_orders` VALUES (1, 'GB202403310001', 2, 1, 299.00, 299.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (2, 'GB202403310002', 3, 2, 499.00, 0.00, 0, 0, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (3, 'GB202403310003', 4, 3, 199.00, 199.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (4, 'GB202403310004', 5, 4, 350.00, 350.00, 0, 1, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (5, 'GB202403310005', 6, 5, 599.00, 599.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (6, 'GB202403310006', 7, 1, 299.00, 0.00, 0, 4, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (7, 'GB202403310007', 8, 2, 499.00, 499.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (8, 'GB202403310008', 9, 3, 199.00, 199.00, 0, 3, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (9, 'GB202403310009', 2, 6, 150.00, 150.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');
INSERT INTO `trade_orders` VALUES (10, 'GB202403310010', 3, 9, 260.00, 260.00, 0, 2, NULL, NULL, NULL, NULL, NULL, '2026-03-31 03:31:04', '2026-03-31 03:31:04');

-- ----------------------------
-- Table structure for trade_payment_records
-- ----------------------------
DROP TABLE IF EXISTS `trade_payment_records`;
CREATE TABLE `trade_payment_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方交易流水号',
  `amount` decimal(10, 2) NOT NULL,
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '0-处理中, 1-成功, 2-失败',
  `raw_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '三方接口原始响应',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录审计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_payment_records
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
