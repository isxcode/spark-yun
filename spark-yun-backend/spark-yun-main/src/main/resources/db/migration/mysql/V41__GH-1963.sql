-- 作业事件表
CREATE TABLE SY_WORK_EVENT
(
    id                      VARCHAR(200) NOT NULL COMMENT '事件id',
    event_process           INT          NOT NULL COMMENT '事件进程',
    event_context           TEXT         NULL COMMENT '事件上下文',
    create_by               VARCHAR(200) NOT NULL COMMENT '创建人',
    create_date_time        DATETIME     NOT NULL COMMENT '创建时间',
    last_modified_by        VARCHAR(200) NOT NULL COMMENT '更新人',
    last_modified_date_time DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '作业事件表';

-- 扩展NODE_MAPPING长度
ALTER TABLE SY_WORKFLOW_CONFIG
    MODIFY COLUMN NODE_MAPPING TEXT;

-- 作业实例绑定eventId
ALTER TABLE SY_WORK_INSTANCE
    ADD COLUMN event_id VARCHAR(100);

-- 删除多余字段
ALTER TABLE SY_WORK_INSTANCE
    DROP COLUMN WORK_PID;