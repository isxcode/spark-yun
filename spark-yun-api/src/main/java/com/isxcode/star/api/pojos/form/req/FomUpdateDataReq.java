package com.isxcode.star.api.pojos.form.req;

import lombok.Data;

@Data
public class FomUpdateDataReq {

  private String formId;

  private Object oldData;

  private Object newData;
}
