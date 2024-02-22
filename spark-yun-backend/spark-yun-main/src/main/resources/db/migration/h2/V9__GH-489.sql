-- 删除资源文件的路径
alter table SY_FILE
    drop column FILE_PATH;

-- 添加备注字段
alter table SY_FILE
    add REMARK varchar2(500);
comment on column SY_FILE.REMARK is '备注';