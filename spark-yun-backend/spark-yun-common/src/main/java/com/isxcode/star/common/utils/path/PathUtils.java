package com.isxcode.star.common.utils.path;

import java.io.File;
import java.io.IOException;

public class PathUtils {

    public static String parseProjectPath(String path) {

        if (path.contains("classpath:")) {
            String replace = path.replace("classpath:", "");
            return System.getProperty("user.dir") + File.separator + replace.replace("/", File.separator);
        }

        return path;
    }

    public static File createFile(String path) throws IOException {
        // 文件路径而非目录
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }
}
