-- 作业事件表
CREATE TABLE SY_WORK_EVENT
(
    id                      VARCHAR(200) NOT NULL,
    event_process           INTEGER      NOT NULL,
    event_context           TEXT         NULL,
    create_by               VARCHAR(200) NOT NULL,
    create_date_time        TIMESTAMP    NOT NULL,
    last_modified_by        VARCHAR(200) NOT NULL,
    last_modified_date_time TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE SY_WORK_EVENT IS '作业事件表';
COMMENT ON COLUMN SY_WORK_EVENT.id IS '事件id';
COMMENT ON COLUMN SY_WORK_EVENT.event_process IS '事件进程';
COMMENT ON COLUMN SY_WORK_EVENT.event_context IS '事件上下文';
COMMENT ON COLUMN SY_WORK_EVENT.create_by IS '创建人';
COMMENT ON COLUMN SY_WORK_EVENT.create_date_time IS '创建时间';
COMMENT ON COLUMN SY_WORK_EVENT.last_modified_by IS '更新人';
COMMENT ON COLUMN SY_WORK_EVENT.last_modified_date_time IS '更新时间';

-- 扩展NODE_MAPPING长度
ALTER TABLE SY_WORKFLOW_CONFIG
    ALTER COLUMN NODE_MAPPING SET DATA TYPE TEXT;

-- 作业实例绑定eventId
ALTER TABLE SY_WORK_INSTANCE
    ADD COLUMN event_id VARCHAR(100);

-- 删除多余字段
ALTER TABLE SY_WORK_INSTANCE
    DROP COLUMN WORK_PID;