-- 修复作业调度信息无法配置分钟级别配置
alter table sy_work_config
    alter column cron_config type varchar(2000) using cron_config::varchar(2000);