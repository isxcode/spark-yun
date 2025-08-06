package com.isxcode.spark.api.view.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigViewReq {

    private String id;

    private Object webConfig;

    private List<String> cardList;
}
