-- 系统监控改用double类型
alter table SY_MONITOR
    alter column USED_STORAGE_SIZE DOUBLE;

alter table SY_MONITOR
    alter column USED_MEMORY_SIZE DOUBLE;