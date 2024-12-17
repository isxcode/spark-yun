-- 修复作业调度信息无法配置分钟级别配置
alter table SY_WORKFLOW_VERSION
    alter column CRON_CONFIG varchar(2000);