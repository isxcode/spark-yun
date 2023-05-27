package com.isxcode.star.api.pojos.work.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WokGetSubmitLogRes {

  private String log;

  private String status;
}
