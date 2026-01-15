-- 用户添加有效期字段
alter table SY_USER
    add valid_start_date_time datetime;

alter table SY_USER
    add valid_end_date_time datetime;

-- 租户添加有效期字段
alter table SY_TENANT
    add valid_start_date_time datetime;

alter table SY_TENANT
    add valid_end_date_time datetime;