-- PRD V4 Database Migration Script
-- Add notes column to biz_projects table
-- ------------------------------------------------------------

ALTER TABLE `biz_projects` ADD COLUMN `notes` TEXT NULL COMMENT '项目注意事项';
