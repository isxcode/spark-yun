package com.isxcode.star.modules.work.service;

import org.springframework.stereotype.Service;

@Service
public class SqlCommentParseService {

	public String removeSqlComment(String sql) {

		// 过滤注释
		String regex = "/\\*(?:.|[\\n\\r])*?\\*/|--.*";
		String noCommentSql = sql.replaceAll(regex, "");
		return noCommentSql.replaceAll("--.*", "").replace("\n", " ");
	}
}
