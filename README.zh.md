[![CI](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[English Version](README.md)

# admin-starter

全栈管理后台 starter，内置基于角色的访问控制（RBAC）。可通过在线演示体验权限同步、角色分配与界面动态显隐，也可在本地运行 Spring Boot 后端与 React 前端。

## 目录

- [演示地址](#演示地址)
- [功能体验流程](#功能体验流程)
- [配置说明](#配置说明)
- [本地运行](#本地运行)
- [贡献](#贡献)

## 演示地址

按网络情况选择访问更快的前端地址：

| 区域 | 地址 |
|------|------|
| 海外 | https://admin-starter.netlify.app/ |
| 中国 | https://admin-starter.u3d.cc/ |

## 功能体验流程

按以下步骤体验 RBAC 核心能力。演示环境可能有其他账号同时操作，请确保你的操作未被他人干扰。如遇问题，请提交 [issue](https://github.com/jinganix/admin-starter/issues)。

### 1. 注册并登录

- 注册账号（例如用户名为 `username`），注册成功后会自动登录。
- 默认情况下，新注册账号会获得**管理员**角色，可执行所有操作（可通过[配置说明](#配置说明)修改此行为）。
- 也可使用内置账号登录：用户名 `admin`，密码 `aaaaaa`。

### 2. 同步权限

- 在左侧栏打开 **系统 → 权限**。
- 点击 **同步 UI**，将前端权限定义写入数据库。
- 点击 **重新加载 API**，将后端 API 权限从代码加载到数据库。

### 3. 创建受限角色

- 打开 **系统 → 角色**，点击 **添加**。
- 创建名为 `role` 的角色，勾选全部权限，**但排除**：
  - **按钮 → 添加用户**
  - **菜单 → 系统 → 审计**
  - **系统 → 用户 → 更新用户状态**

### 4. 将受限角色分配给自己的用户

- 打开 **系统 → 用户**，搜索 `username`。
- 点击 **··· → 编辑**，角色仅选择新建的 `role` 并保存。
- 此时该用户拥有 `role` 的全部权限，上述三项除外。

### 5. 验证限制效果

- 以 `username` 登录（若已是该用户，可点击右上角头像 → **刷新**）。
- 预期变化：
  - 不再显示 **+ 添加** 按钮（`role` 无 **按钮 → 添加用户** 权限）。
  - 左侧栏不再显示 **系统 → 审计** 菜单（`role` 无 **菜单 → 系统 → 审计** 权限）。
  - 对除 `admin` 以外用户点击状态开关会跳转到错误页（`role` 无 **系统 → 用户 → 更新用户状态** 接口权限）。

### 6. 恢复完整权限

- 在 **系统 → 角色** 中为 `role` 勾选全部权限，再次刷新用户数据，即可看到界面恢复。

## 配置说明

注册时的角色分配由 [service/src/main/resources/application.yml](service/src/main/resources/application.yml) 控制：

```yaml
config:
  signup:
    register-as-admin: true
```

| 值 | 行为 |
|----|------|
| `true` | **默认值。** 新注册用户获得管理员角色，可访问全部功能。适用于演示和本地开发。 |
| `false` | 新注册用户不分配任何数据库角色，仅可访问个人相关接口（资料、密码），需管理员后续分配业务角色。适用于生产环境。 |

运行时覆盖示例：

```shell
./gradlew service:bootRun --args='--config.signup.register-as-admin=false'
```

## 本地运行

### 环境要求

| 工具 | 说明 |
|------|------|
| JDK | 版本见 [.tool-versions](.tool-versions) |
| Node.js | 版本见 [.tool-versions](.tool-versions) |
| MySQL & Redis | 使用 Gradle 启动后端时需要（Docker Compose 已包含） |
| Docker & Docker Compose | 可选；推荐用于启动后端 |

### 克隆仓库

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter
```

### 后端

#### 方式一：Docker Compose（推荐）

```shell
./gradlew build
docker-compose up --build
```

API 地址：`http://localhost:8080`。

#### 方式二：Gradle

1. 安装 [.tool-versions](.tool-versions) 中指定的 JDK。
2. 本地启动 MySQL 与 Redis。
3. 若连接配置与默认不一致，请修改 [service/src/main/resources/application-local.yml](service/src/main/resources/application-local.yml)。

**Linux / macOS：**

```shell
./gradlew service:bootRun
```

**Windows：**

```shell
./gradlew.bat service:bootRun
```

### 前端

在项目根目录执行：

```shell
cd frontend
npm install
npm start
```

## 贡献

如有兴趣报告或修复问题、直接参与代码贡献，请参阅 [CONTRIBUTING.md](CONTRIBUTING.md) 了解期望与入门方式。
