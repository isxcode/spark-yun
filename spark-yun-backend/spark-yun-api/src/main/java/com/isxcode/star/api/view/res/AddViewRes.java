package com.isxcode.star.api.view.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddViewRes {

    private String id;

    private String name;

    private String status;
}
