-- 作业事件表
create table SY_WORK_EVENT
(
    id                      varchar(200) not null comment '事件id' primary key,
    event_process           int          not null comment '事件进程',
    event_context           text         null comment '事件上下文',
    create_by               varchar(200) not null comment '创建人',
    create_date_time        datetime     not null comment '创建时间',
    last_modified_by        varchar(200) not null comment '更新人',
    last_modified_date_time datetime     not null comment '更新时间'
);

-- 扩展NODE_MAPPING长度
alter table SY_WORKFLOW_CONFIG
    alter column NODE_MAPPING text;

-- 作业实例绑定eventId
alter table SY_WORK_INSTANCE
    add event_id varchar(100);