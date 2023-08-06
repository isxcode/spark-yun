package com.isxcode.star.api.form.pojos.req;

import lombok.Data;

@Data
public class FomUpdateDataReq {

  private String formId;

  private Object oldData;

  private Object newData;
}
