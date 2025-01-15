package com.isxcode.star.api.agent.req;

import com.isxcode.star.api.func.dto.FuncInfo;
import lombok.Data;

import java.util.List;

@Data
public class SubmitWorkReq {

    private PluginReq pluginReq;

    private SparkSubmit sparkSubmit;

    private String agentHomePath;

    private String clusterType;

    private String sparkHomePath;

    private String[] args;

    private String argsStr;

    private String workType;

    private String workId;

    private List<String> libConfig;

    private List<FuncInfo> funcConfig;

    private String workInstanceId;
}
