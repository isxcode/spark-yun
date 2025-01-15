package com.isxcode.star.api.real.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRealSubmitLogRes {

    private String id;

    private String status;

    private String submitLog;
}
