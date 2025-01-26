[![CI](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/jinganix/admin-starter/actions/workflows/ci.yml)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[English Version](README.md)

# admin-starter

## 演示地址

请选择速度较快的前端页面地址，查看DEMO：

- 海外地址：https://admin-starter.netlify.app/
- 中国地址：https://admin-starter.u3d.cc/

## 用户体验旅程

你可以按以下流程体验基本功能。

需要注意的是，在你操作的过程中，可能有其他账号在并发操作。

确保你的操作步骤未被其他账号影响，如你发现任何问题，请提交[issue](https://github.com/jinganix/admin-starter/issues)。

- 注册账号，假设用户名为`username`，注册成功会自动登录进入系统
  - 新注册的账号，默认具有管理员角色。

- 点击左侧`系统` - `权限`，进入权限管理页面，表格右上角有`同步UI`和`重新加载API`按钮
  - 点击`同步UI`即可同步前端权限到数据库
  - 点击`重新加载API`即可加载所有后端API权限到数据库

- 点击左侧`系统` - `角色`，进入角色管理页面。
- 点击`添加`按钮，添加一个角色，假设角色名为`role`，勾选所有权限，以下权限除外：
  - `按钮` - `添加用户`
  - `菜单` - `系统` - `审计`
  - `系统` - `用户` - `更新用户状态`

- 点击左侧`系统` - `用户`，进入用户管理页面，通过搜索`username`找到自己的用户
- 点击右边`···` - `编辑`，在编辑框的角色选项处，只选择刚刚创建的角色`role`，保存修改
  - 此时你的用户`username`拥有了`role`角色和`role`的所有权限，刚刚未选择的三个权限除外

- 点击页面右上角圆形图标，选择`刷新`来刷新自己的用户数据和权限数据，数据刷新后有如下变化
  - 页面不再显示`+ 添加`按钮，因为`role`不具有`按钮` - `添加用户`权限
  - 左边栏不再显示`系统` - `审计`菜单，因为`role`不具有`菜单` - `系统` - `审计`权限
  - 点击除`admin`用户以外的状态切换按钮，你将被重定向到错误页面，因为`role`不具有`系统` - `用户` - `更新用户状态`的接口权限

- 最后你可以在`系统` - `角色`页面，将所有的权限赋予`role`角色，刷新用户数据后查看效果

## 运行项目

你需要先拉取项目源码，切换到项目根目录，使用gradle构建项目

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter
```

### 后端

#### 通过docker-compose运行

如果你已经安装`docker`和`docker-compose`，可以通过以下命令启动后端服务

```shell
./gradlew build
docker-compose up --build
```

#### 通过gradle运行

你需要安装jdk，对应的版本在[.tool-versions](.tool-versions)，并启动`MySQL`数据库和`Redis`。

修改[application-local.yml](service/admin-starter/src/main/resources/application-local.yml)以运行项目后端服务。

以下命令可以在`Linux`或`MacOS`系统中启动后端服务

```shell
./gradlew build
./gradlew service:admin-starter:bootRun
```

以下命令可以在`Windows`系统中启动后端服务

```shell
./gradlew.bat service:admin-starter:bootRun
```

### 前端

你需要安装[node.js](https://nodejs.org/en)，对应的版本在[.tool-versions](.tool-versions)

#### 运行命令

```shell
git clone git@github.com:jinganix/admin-starter.git
cd admin-starter/frontend/admin
npm install
npm start
```

## 贡献

如果你有兴趣报告/修复问题并直接为代码库做出贡献，请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 获取更多信息，了解我们期望的贡献内容以及如何开始。
