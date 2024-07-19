-- 扩长running_log
alter table SY_REAL
    modify running_log longtext null comment '运行日志'