package com.isxcode.star.api.api.pojos.res;

import com.isxcode.star.api.api.pojos.dto.GetReqParamDto;
import com.isxcode.star.api.api.pojos.dto.HeaderTokenDto;
import lombok.Data;

import java.util.List;

@Data
public class GetApiRes {

    private String id;

    private String name;

    private String path;

    private String remark;

    private String apiType;

    private String tokenType;

    private List<HeaderTokenDto> reqHeader;

    private String reqBody;

    private String apiSql;

    private String resBody;

    private String datasourceId;

    private Boolean pageType;

    private String reqJsonTemp;

    private List<GetReqParamDto> reqGetTemp;
}
