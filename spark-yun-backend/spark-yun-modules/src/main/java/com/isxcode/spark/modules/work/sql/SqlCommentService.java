package com.isxcode.spark.modules.work.sql;

import org.springframework.stereotype.Service;

@Service
public class SqlCommentService {

    /**
     * 逐行判断，每行前面是`--`则改为换行符.
     */
    public String removeSqlComment(String sql) {

        String[] lines = sql.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("--")) {
                result.append(line).append("\n");
            } else {
                result.append("\n");
            }
        }
        return result.toString();
    }
}
