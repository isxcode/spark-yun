package com.isxcode.star.api.form.constants;

import java.util.Arrays;

public enum ComponentType {

	/**
	 * 文本组件.
	 */
	FORM_INPUT_TEXT("文本组件", "文本输入", "请输入"),
	/**
	 * 数字组件.
	 */
	FORM_INPUT_NUMBER("数字组件", "数字输入", "请输入"),
	/**
	 * 金额组件.
	 */
	FORM_INPUT_MONEY("金额组件", "金额输入", "请输入"),
	/**
	 * 手机号组件.
	 */
	FORM_INPUT_PHONE("手机号组件", "手机号输入", "请输入"),
	/**
	 * 邮箱组件
	 */
	FORM_INPUT_EMAIL("邮箱组件", "邮箱输入", "请输入"),
	/**
	 * 下拉组件.
	 */
	FORM_INPUT_SELECT("下拉组件", "下拉选择", "请选择"),
	/**
	 * 日期组件.
	 */
	FORM_INPUT_DATE("日期组件", "日期选择", "请选择"),
	/**
	 * 开关组件.
	 */
	FORM_INPUT_SWITCH("开关组件", "开关切换", "请选择"),
	/**
	 * 单选组件.
	 */
	FORM_INPUT_RADIO("单选组件", "单项选择", "请选择"),
	/**
	 * 多选组件
	 */
	FORM_INPUT_CHECKBOX("多选组件", "多项选择", "请选择"),

	;

	private final String label;
	private final String desc;
	private final String placeholder;

	public String getLabel() {
		return label;
	}

	public String getDesc() {
		return desc;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	ComponentType(String label, String desc, String placeholder) {
		this.label = label;
		this.desc = desc;
		this.placeholder = placeholder;
	}

	/**
	 * 选择类组件
	 */
	public static final ComponentType[] SELECT = {FORM_INPUT_SELECT, FORM_INPUT_RADIO, FORM_INPUT_CHECKBOX};
	/**
	 * 输入类组件
	 */
	public static final ComponentType[] TEXT = {FORM_INPUT_TEXT, FORM_INPUT_PHONE, FORM_INPUT_EMAIL, FORM_INPUT_DATE};
	/**
	 * 数字类组件
	 */
	public static final ComponentType[] NUMBER = {FORM_INPUT_NUMBER, FORM_INPUT_MONEY};
	/**
	 * 布尔类组件
	 */
	public static final ComponentType[] BOOLEAN = {FORM_INPUT_SWITCH};

	public static final String[] SELECT_NAME = {FORM_INPUT_SELECT.name(), FORM_INPUT_RADIO.name(),
			FORM_INPUT_CHECKBOX.name()};
	public static final String[] TEXT_NAME = {FORM_INPUT_TEXT.name(), FORM_INPUT_PHONE.name(), FORM_INPUT_EMAIL.name(),
			FORM_INPUT_DATE.name()};
	public static final String[] NUMBER_NAME = {FORM_INPUT_NUMBER.name(), FORM_INPUT_MONEY.name()};
	public static final String[] BOOLEAN_NAME = {FORM_INPUT_SWITCH.name()};

	public static boolean isSelectType(String componentType) {
		return Arrays.asList(SELECT_NAME).contains(componentType);
	}
}
