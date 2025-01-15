package com.isxcode.star.api.work.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SyncColumnInfo implements Serializable {

    private String code;

    private String type;

    private String sql;

    private String jsonPath;
}
