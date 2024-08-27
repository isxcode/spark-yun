package com.isxcode.star.modules.work.sql;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Service
public class SqlValueService {

    /**
     * 变量值为 ${qing.currentDate}.
     */
    public String parseSqlValue(String sql) {

        Pattern pattern = compile("(\\$\\{).+?}");
        Matcher matcher = pattern.matcher(sql);

        // 实时解析当前变量
        Map<String, String> valueMap = getValueMap();

        // 替换正则
        while (matcher.find()) {
            String group = matcher.group();
            if (valueMap.get(group.trim()) != null) {
                sql = sql.replace(group, valueMap.get(group.trim()));
            }
        }

        return sql;
    }

    public Map<String, String> getValueMap() {

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("${qing.current_date}", getCurrentDate());
        valueMap.put("${qing.current_year}", getCurrentYear());
        valueMap.put("${qing.current_month}", getCurrentMonth());
        valueMap.put("${qing.current_date1}", getCurrentDate1());
        return valueMap;
    }

    /**
     * 返回当前时间2020-12-12.
     */
    public String getCurrentDate() {

        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 返回当前时间2020.
     */
    public String getCurrentYear() {

        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
    }

    /**
     * 返回当前时间12.
     */
    public String getCurrentMonth() {

        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    /**
     * 返回当前时间20201212.
     */
    public String getCurrentDate1() {

        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
