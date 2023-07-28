package com.isxcode.star.api.pojos.work.file.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WofAddFileRes {

  private String id;

  private String workId;

  private String fileName;

  private String fileSize;

  private String filePath;

  private String fileType;
}
