# MiniHub - Velocity 插件

> **AI 生成声明**: 本项目由 AI 辅助生成，代码仅供正常使用和参考和学习使用，请不要将其再次投入AI训练！

一个简单的 Velocity 代理服务器插件，允许玩家通过配置的命令传送到指定的服务器。

## 功能特性

- 🚀 通过简单命令传送到指定服务器。可以用中文命令！

## 安装

1. 下载最新的 MiniHub jar 文件
2. 将 jar 文件放入 Velocity 的 `plugins` 目录
3. 重启 Velocity 服务器
4. 插件会自动生成配置文件和语言文件

## 配置

配置文件位于 `plugins/MiniHub/config.yml`：

```yaml
# MiniHub 插件配置文件

# 使用的语言
language: "zh_cn"

# 服务器命令配置
# 格式: 服务器名称 -> 命令列表
servers:
  lobby:
    commands: ["hub", "lobby"]
    #使用/hub或/lobby均可传送到服务器lobby
  survival:
    commands: ["survival", "srv"]
  creative:
    commands: ["creative", "crea"]
```

## 命令使用

根据配置文件中的设置，玩家可以使用相应的命令传送到目标服务器：

- `/hub` 或 `/lobby` - 传送到大厅服务器
- `/survival` 或 `/srv` - 传送到生存服务器  
- `/creative` 或 `/crea` - 传送到创造服务器

## 开发信息

- **开发语言**: Java 17
- **构建工具**: Maven
- **依赖管理**: Maven
- **支持平台**: Velocity Proxy


## 更新日志

### v1.0-SNAPSHOT
- 初始版本发布
- 基础命令传送功能
- 多语言支持
- 配置文件系统