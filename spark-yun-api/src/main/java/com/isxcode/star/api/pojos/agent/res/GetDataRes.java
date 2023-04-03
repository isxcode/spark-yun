package com.isxcode.star.api.pojos.agent.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDataRes {

  private List<List> data;

  private String message;

  private String log;
}
