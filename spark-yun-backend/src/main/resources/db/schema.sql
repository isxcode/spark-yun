create table if not exists sy_users
(
  id       varchar(100) null,
  account  varchar(100) null,
  password varchar(100) null,
  email    varchar(100) null
);

create table if not exists sy_engines
(
  id             varchar(100) null,
  name           varchar(100) null,
  comment_info   varchar(100) null,
  status         varchar(100) null,
  check_date     datetime     null,
  all_node       int          null,
  active_node    int          null,
  all_memory     int          null,
  active_memory  int          null,
  all_storage    int          null,
  active_storage int          null
);

create table if not exists sy_nodes
(
  id             varchar(100) null,
  name           varchar(100) null,
  comment_info   varchar(100) null,
  status         varchar(100) null,
  check_date     datetime     null,
  all_memory     int          null,
  active_memory  int          null,
  all_storage    int          null,
  active_storage int          null,
  cpu_percent    varchar(100) null,
  engine_id      varchar(100) null,
  host           varchar(100) null,
  port           varchar(100) null,
  username       varchar(100) null,
  passwd         varchar(100) null,
  home_path      varchar(100) null
);

create table if not exists sy_datasources
(
  id           varchar(100) null,
  name         varchar(100) null,
  jdbc_url     varchar(200) null,
  comment_info varchar(100) null,
  status       varchar(100) null,
  check_date   datetime     null,
  username     varchar(100) null,
  type         varchar(100) null,
  passwd       varchar(100) null
);

create table if not exists sy_workflows
(
  id           varchar(100) null,
  name         varchar(100) null,
  comment_info varchar(100) null,
  status       varchar(100) null,
  label        varchar(100) null
);

create table if not exists sy_works
(
  id               varchar(100) null,
  name             varchar(100) null,
  comment_info     varchar(100) null,
  status           varchar(100) null,
  label            varchar(100) null,
  type             varchar(100) null,
  work_config_id   varchar(100) null,
  create_date_time datetime     null,
  workflow_id      varchar(100) null
);

create table if not exists sy_work_configs
(
  id            varchar(100) null,
  datasource_id varchar(100) null,
  engine_id     varchar(100) null,
  script        varchar(100) null
);
