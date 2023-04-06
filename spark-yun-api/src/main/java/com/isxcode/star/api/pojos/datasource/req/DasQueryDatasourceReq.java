package com.isxcode.star.api.pojos.datasource.req;

import lombok.Data;

@Data
public class DasQueryDatasourceReq {

  private Integer page;

  private Integer pageSize;

  private String searchContent;
}
