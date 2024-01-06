-- 新增字段 createMode 创建模式
alter table SY_FORM
  add create_mode varchar(100) null comment '表单创建模式' after status;

-- 新增字段 createMode 创建模式
alter table SY_FORM
  add remark varchar(500) null comment '表单备注' after create_mode;
