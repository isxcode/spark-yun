-- 表单新增表单备注字段
alter table SY_FORM
  add remark varchar(500) null commen t '表单备注' after create_mode;