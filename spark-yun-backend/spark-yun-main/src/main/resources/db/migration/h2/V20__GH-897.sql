-- 添加外部调用开关状态
alter table SY_WORKFLOW_CONFIG
    add INVOKE_STATUS varchar(100) default 'OFF' not null;

comment on column SY_WORKFLOW_CONFIG.INVOKE_STATUS is '是否启动外部调用';