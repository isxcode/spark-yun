-- 添加下次执行时间
alter table SY_WORKFLOW
    add next_date_time datetime;

comment on column SY_WORKFLOW.next_date_time is '下次执行时间';