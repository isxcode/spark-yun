package com.isxcode.star.utils.path;

import java.io.File;

public class PathUtils {

  public static String parseProjectPath(String path) {

    if (path.contains("classpath:")) {
      String replace = path.replace("classpath:", "");
      return System.getProperty("user.dir") + File.separator + replace.replace("/", File.separator);
    }

    return path;
  }
}
