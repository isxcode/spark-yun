package com.isxcode.star.api.view.pojo.res;

import com.isxcode.star.api.view.pojo.dto.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetViewCardRes {

    private CardInfo cardInfo;
}
