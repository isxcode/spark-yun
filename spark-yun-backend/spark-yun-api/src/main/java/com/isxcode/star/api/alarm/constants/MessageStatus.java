package com.isxcode.star.api.alarm.constants;

/**
 * 消息体的状态.
 */
public interface MessageStatus {

    /**
     * 邮件.
     */
    String NEW = "NEW";

    /**
     * 未检测.
     */
    String UN_CHECK = "UN_CHECK";

    /**
     * 启用.
     */
    String ACTIVE = "ACTIVE";

    /**
     * 检测成功.
     */
    String CHECK_SUCCESS = "CHECK_SUCCESS";

    /**
     * 检测失败.
     */
    String CHECK_FAIL = "CHECK_FAIL";

    /**
     * 禁用.
     */
    String DISABLE = "DISABLE";
}
