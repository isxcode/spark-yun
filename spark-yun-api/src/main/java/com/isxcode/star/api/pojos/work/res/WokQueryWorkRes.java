package com.isxcode.star.api.pojos.work.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WokQueryWorkRes {

  private String remark;

  private String name;

  private String id;

  private String workType;

  private String createDateTime;

  private String status;

  private String corn;
}
