package com.isxcode.star.api.pojos.yun.agent.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.isxcode.star.api.pojos.spark.BaseReturn;
import java.util.List;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YagGetDataRes extends BaseReturn {

  private String applicationId;

  @Builder(builderMethodName = "yagGetDataResBuilder")
  public YagGetDataRes(List<String> column, List<List<String>> data, String applicationId) {
    super(column, data);
    this.applicationId = applicationId;
  }
}
