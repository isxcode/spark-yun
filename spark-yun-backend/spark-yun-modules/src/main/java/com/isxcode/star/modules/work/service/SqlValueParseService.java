package com.isxcode.star.modules.work.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Service
public class SqlValueParseService {

	/**
	 * ${ qing.currentDate }.
	 */
	public String setSqlValue(String sql) {

		// 正则解析${}
		Pattern pattern = compile("(\\$\\{).+?}");
		Matcher matcher = pattern.matcher(sql);

		Map<String, String> qingValueMap = getQingValueMap();
		while (matcher.find()) {
			String group = matcher.group();
			String key = group.replace("${", "").replace("}", "");
			String[] split = key.split("\\.");
			if ("qing".equals(split[0])) {
				sql = sql.replace(group, qingValueMap.get(split[1]));
			}
		}

		return sql;
	}

	public Map<String, String> getQingValueMap() {

		Map<String, String> qingValueMap = new HashMap<>();
		qingValueMap.put("currentDate", getCurrentDate());

		return qingValueMap;
	}

	public String getCurrentDate() {

		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

}
