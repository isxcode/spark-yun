package com.isxcode.star.api.pojos.form.req;

import com.isxcode.star.api.pojos.form.dto.FomComponentDto;
import lombok.Data;

import java.util.List;

@Data
public class FomAddFormReq {

  private String name;

  private String datasourceId;

  private String mainTable;

  private String insertSql;

  private String deleteSql;

  private String updateSql;

  private String selectSql;

  private List<FomComponentDto> components;
}
