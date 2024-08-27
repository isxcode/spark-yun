package com.isxcode.star.modules.work.sql;

import org.springframework.stereotype.Service;

@Service
public class SqlCommentService {

    public String removeSqlComment(String sql) {

        String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
        String noCommentSql = sql.replaceAll(regex, "");
        return noCommentSql.replaceAll("--.*", "").replace("\n", " ");
    }
}
