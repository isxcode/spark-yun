package com.isxcode.star.api.instance.pojos.res;

import lombok.Data;


@Data
public class GetWorkInstanceValuePathRes {

    private String jsonPath;

    private String value;

    private String copyValue;
}
