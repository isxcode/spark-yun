-- 系统监控改用double类型
alter table SY_MONITOR
    alter column DISK_IO_READ_SPEED DOUBLE;

alter table SY_MONITOR
    alter column DISK_IO_WRITE_SPEED DOUBLE;

alter table SY_MONITOR
    alter column NETWORK_IO_WRITE_SPEED DOUBLE;

alter table SY_MONITOR
    alter column NETWORK_IO_READ_SPEED DOUBLE;
