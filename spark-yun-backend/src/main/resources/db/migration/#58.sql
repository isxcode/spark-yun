-- 新增字段
alter table SY_USER add user_role varchar(200) not null comment '用户权限';
alter table SY_USER add create_by varchar(200) not null comment '创建者';
alter table SY_USER add last_modified_by varchar(200) not null comment '更新者';
alter table SY_USER add create_date_time varchar(200) not null comment '创建时间';
alter table SY_USER add last_modified_date_time varchar(200) not null comment '更新时间';
alter table SY_USER add version_number int not null comment '版本号';

-- 配置管理员信息
update SY_USER set USER_ROLE = 'SYS_ADMIN' where ID='admin_id'

alter table SY_USER drop column status;
alter table SY_USER add status varchar(200) not null comment '用户状态';
-- alter table SY_USER alter column status varchar(200) comment '用户状态';
