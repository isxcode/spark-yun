-- 修复作业调度信息无法配置分钟级别配置
alter table SY_WORK_CONFIG
    alter column CRON_CONFIG varchar(2000);