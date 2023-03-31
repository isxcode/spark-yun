package com.isxcode.star.api.pojos.agent.res;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetDataRes {

  private List<List> data;

  private String message;

  private String log;
}
