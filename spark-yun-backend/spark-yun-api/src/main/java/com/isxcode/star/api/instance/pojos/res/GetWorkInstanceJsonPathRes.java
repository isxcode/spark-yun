package com.isxcode.star.api.instance.pojos.res;

import com.isxcode.star.api.instance.pojos.dto.WorkInstanceDto;
import lombok.Data;

import java.util.List;

@Data
public class GetWorkInstanceJsonPathRes {

    private String jsonPath;

    private String value;

    private String copyValue;
}
