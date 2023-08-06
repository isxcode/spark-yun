package com.isxcode.star.api.work.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WokGetSubmitLogRes {

  private String log;

  private String status;
}
