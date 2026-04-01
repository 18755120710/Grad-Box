# Grad-Box

Grad-Box 是一个综合性的项目管理与竞赛系统，包含管理后台、用户前台及核心后端服务。

## 📁 项目结构
- **`code/admin`**: 管理员后台（基于 Vue/Nuxt），用于管理用户、项目、竞赛及系统配置。
- **`code/user`**: 用户中心与前台展示，为参赛者提供报名、资料管理及进度追踪功能。
- **`code/backent_init`**: 后端核心服务（基于 Spring Boot），提供 RESTful API、JWT 认证及数据库交互。
- **`code/db`**: 数据库资源，包含初始化 SQL 脚本及核心数据定义。

## 🛠 技术栈
- **前端 (Frontend)**: Vue.js 3, Nuxt.js, Tailwind CSS, ECharts
- **后端 (Backend)**: Spring Boot, MyBatis-Plus, JWT, Spring Security
- **数据库 (Database)**: MySQL
- **包管理器 (Package Manager)**: pnpm (前端), Maven (后端)

## 🚀 快速启动
### 1. 数据库配置
1. 执行 `code/db/init.sql` 以初始化数据库表结构。
2. 在 `code/backent_init/src/main/resources/application.yml` 中配置你的 MySQL 连接地址。

### 2. 后端运行
```bash
cd code/backent_init
mvn spring-boot:run
```

### 3. 前端运行
```bash
# 管理员后台
cd code/admin
pnpm install
pnpm dev

# 用户中心
cd code/user
pnpm install
pnpm dev
```

## 📝 提交规范
本仓库采用统一管理模式，请勿在子文件夹中保留独立的 `.git` 目录。提交代码时请使用有意义的中文描述。
