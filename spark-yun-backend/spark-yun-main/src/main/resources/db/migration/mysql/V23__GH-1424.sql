-- 修复作业调度信息无法配置分钟级别配置
alter table SY_WORK_CONFIG
    modify cron_config varchar(2000) null comment '定时表达式';