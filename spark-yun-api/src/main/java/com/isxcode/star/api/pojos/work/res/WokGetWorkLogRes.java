package com.isxcode.star.api.pojos.work.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WokGetWorkLogRes {

  private List<String> logList;
}
