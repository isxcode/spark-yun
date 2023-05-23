package com.isxcode.star.api.pojos.form.req;

import com.isxcode.star.api.pojos.form.dto.FomComponentDto;
import java.util.List;
import lombok.Data;

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
