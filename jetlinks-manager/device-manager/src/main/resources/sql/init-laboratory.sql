-- 实验室信息表
CREATE TABLE IF NOT EXISTS dev_laboratory (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    code VARCHAR(64) NOT NULL COMMENT '实验室编码',
    name VARCHAR(255) NOT NULL COMMENT '实验室名称',
    description TEXT COMMENT '实验室描述',
    address VARCHAR(500) COMMENT '实验室地址',
    manager VARCHAR(100) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    device_count INT DEFAULT 0 COMMENT '设备数量',
    parent_id VARCHAR(64) COMMENT '父级ID',
    path VARCHAR(500) COMMENT '树结构路径',
    sort_index INT DEFAULT 0 COMMENT '排序序号',
    level INT DEFAULT 1 COMMENT '树层级',
    creator_id VARCHAR(64) COMMENT '创建者ID',
    create_time BIGINT COMMENT '创建时间',
    update_time BIGINT COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path),
    INDEX idx_status (status)
) COMMENT='实验室信息表';

-- 实验室设备关联表
CREATE TABLE IF NOT EXISTS dev_laboratory_device (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    laboratory_id VARCHAR(64) NOT NULL COMMENT '实验室ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    assign_time BIGINT COMMENT '分配时间',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    creator_id VARCHAR(64) COMMENT '创建者ID',
    create_time BIGINT COMMENT '创建时间',
    update_time BIGINT COMMENT '更新时间',
    UNIQUE KEY uk_laboratory_device (laboratory_id, device_id),
    INDEX idx_laboratory_id (laboratory_id),
    INDEX idx_device_id (device_id),
    INDEX idx_status (status)
) COMMENT='实验室设备关联表';

-- 插入默认数据
INSERT INTO dev_laboratory (id, code, name, description, status, device_count, sort_index, level, create_time) VALUES
('lab_001', 'LAB_001', '主实验室', '主要实验区域', 1, 0, 1, 1, UNIX_TIMESTAMP() * 1000),
('lab_002', 'LAB_002', '测试实验室', '测试专用实验室', 1, 0, 2, 1, UNIX_TIMESTAMP() * 1000),
('lab_003', 'LAB_003', '研发实验室', '研发专用实验室', 1, 0, 3, 1, UNIX_TIMESTAMP() * 1000);
