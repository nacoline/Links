# 实验室管理功能

## 功能概述

实验室管理功能允许用户创建和管理实验室，并将设备分配到不同的实验室中，实现按实验室分类展示设备。

## 主要功能

### 1. 实验室管理
- 新增实验室
- 编辑实验室信息
- 删除实验室
- 查看实验室列表

### 2. 设备分配
- 查看实验室下的设备
- 分配设备到实验室
- 从实验室移除设备

### 3. 树形结构支持
- 支持实验室的层级结构
- 支持实验室的排序

## 技术架构

### 实体类
- `DeviceLaboratoryEntity`: 实验室信息实体
- `DeviceLaboratoryDeviceEntity`: 实验室设备关联实体

### 服务类
- `DeviceLaboratoryService`: 实验室管理服务
- `DeviceLaboratoryDeviceService`: 实验室设备关联服务

### 控制器
- `DeviceLaboratoryController`: 实验室管理API控制器

## API接口

### 实验室管理
- `GET /device/laboratory` - 获取实验室列表
- `POST /device/laboratory` - 新增实验室
- `PUT /device/laboratory/{id}` - 更新实验室
- `DELETE /device/laboratory/{id}` - 删除实验室

### 树形结构
- `GET /device/laboratory/_tree` - 获取实验室树形结构
- `POST /device/laboratory/_tree` - 根据查询条件获取实验室树形结构

### 设备管理
- `GET /device/laboratory/{laboratoryId}/devices` - 获取实验室下的设备列表
- `POST /device/laboratory/{laboratoryId}/devices` - 分配设备到实验室
- `DELETE /device/laboratory/{laboratoryId}/devices` - 从实验室移除设备

## 数据库设计

### dev_laboratory 表
- 基本信息：编码、名称、描述、地址、负责人、联系电话、邮箱
- 状态管理：状态、设备数量
- 树形结构：父级ID、路径、排序序号、层级
- 审计信息：创建者ID、创建时间、更新时间

### dev_laboratory_device 表
- 关联信息：实验室ID、设备ID
- 状态管理：状态
- 审计信息：分配时间、创建者ID、创建时间、更新时间

## 使用说明

1. 启动应用后，系统会自动创建相关数据表
2. 可以通过API接口进行实验室的增删改查操作
3. 支持实验室的树形结构管理
4. 可以分配和移除实验室下的设备
5. 支持按实验室筛选设备

## 注意事项

1. 实验室编码必须唯一
2. 删除实验室前需要先移除所有关联的设备
3. 树形结构支持无限层级，但建议控制在合理范围内
4. 设备分配时会自动更新实验室的设备数量统计
