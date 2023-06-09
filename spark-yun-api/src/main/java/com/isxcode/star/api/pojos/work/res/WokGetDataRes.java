package com.isxcode.star.api.pojos.work.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WokGetDataRes {

  private List<List<String>> data;
}
