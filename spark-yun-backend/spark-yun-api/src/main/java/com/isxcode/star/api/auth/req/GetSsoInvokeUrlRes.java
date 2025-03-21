package com.isxcode.star.api.auth.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetSsoInvokeUrlRes {

    private String invokeUrl;
}
