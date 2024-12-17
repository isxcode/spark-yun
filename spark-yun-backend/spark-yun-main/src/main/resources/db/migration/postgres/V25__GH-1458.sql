-- 修复分享表单新建问题
alter table sy_form
    alter column insert_sql drop not null;

alter table sy_form
    alter column delete_sql drop not null;

alter table sy_form
    alter column update_sql drop not null;

alter table sy_form
    alter column select_sql drop not null;