package com.isxcode.star.api.pojos.work.res;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WokGetDataRes {

  private List<List<String>> data;
}
