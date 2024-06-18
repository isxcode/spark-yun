package com.isxcode.star.modules.work.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

@Service
public class SqlFunctionParseService {

	public static String groovyImport = "";

	public static String dateToStrFuncStr = "def greet(name) { return \"Hello, name!\" } \n";

	public static String dateAddFuncStr = "def sum(a, b) { return a + b } \n";

	public static String groovyPrefixStr = groovyImport + dateToStrFuncStr + dateAddFuncStr + "\n";

	// public String setFunctionValue(String sql) {
	public static void main(String[] args) {

		// 创建一个绑定对象
		Binding binding = new Binding();

		// 创建 GroovyShell
		GroovyShell shell = new GroovyShell(binding);

		// 评估 Groovy 脚本字符串
		try {
			shell.evaluate(groovyPrefixStr + "result = sum(12,1)");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object greeting = String.valueOf(binding.getVariable("result"));

		System.out.println(greeting);
	}

}
