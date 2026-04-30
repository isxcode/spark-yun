package com.isxcode.spark.modules.work.sql;

import org.springframework.stereotype.Service;

@Service
public class SqlCommentService {

    /**
     * 逐行去除SQL注释，支持行首和行中的`--`注释. 字符串引号内的`--`不会被当作注释处理.
     */
    public String removeSqlComment(String sql) {

        String[] lines = sql.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String processed = removeLineComment(line);
            if (!processed.trim().isEmpty()) {
                result.append(processed).append("\n");
            }
        }
        return result.toString();
    }

    /**
     * 去除单行中的--注释，忽略单引号和双引号内的--.
     */
    private String removeLineComment(String line) {

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == '-' && !inSingleQuote && !inDoubleQuote && i + 1 < line.length()
                && line.charAt(i + 1) == '-') {
                return line.substring(0, i).replaceAll("\\s+$", "");
            }
        }
        return line;
    }
}
