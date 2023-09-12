package com.isxcode.star.api.file.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddFileRes {

  private String id;

  private String workId;

  private String fileName;

  private String fileSize;

  private String filePath;

  private String fileType;
}
