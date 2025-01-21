package com.isxcode.star.common.utils.os;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OsUtils {

    public static String fixWindowsChar(String path, String command) {

        log.debug("os.name {}", System.getProperty("os.name"));

        return System.getProperty("os.name").contains("Windows") ? "sed -i 's/\\r//g' " + path + " && " + command
            : command;
    }
}
