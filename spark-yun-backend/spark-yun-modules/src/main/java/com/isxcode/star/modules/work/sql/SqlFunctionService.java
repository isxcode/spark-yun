package com.isxcode.star.modules.work.sql;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Service
@RequiredArgsConstructor
public class SqlFunctionService {

    private final ResourceLoader resourceLoader;

    /**
     * 解析函数 #{ add_date(${qing.currentDate},1) }.
     */
    public String parseSqlFunction(String sql) {

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);

        Pattern pattern = compile("(#\\[\\[).+?]]");
        Matcher matcher = pattern.matcher(sql);

        // 获取groovy函数脚本
        String groovyFunctions = getGroovyFunctions();

        // 替换正则
        while (matcher.find()) {
            String group = matcher.group();
            String functionStr = group.replace("#[[", "").replace("]]", "");
            shell.evaluate(groovyFunctions + "result=" + functionStr);
            String result = String.valueOf(binding.getVariable("result"));
            sql = sql.replace(group, result);
        }
        return sql;
    }

    /**
     * 获取groovy函数.
     */
    public String getGroovyFunctions() {

        try {
            Resource resource = resourceLoader.getResource("classpath:functions.groovy");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        } catch (IOException e) {
            throw new IsxAppException("获取groovy函数异常");
        }
    }

}
