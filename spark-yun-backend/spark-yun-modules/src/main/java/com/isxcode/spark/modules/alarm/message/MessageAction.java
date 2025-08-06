package com.isxcode.spark.modules.alarm.message;

public interface MessageAction {

    String getActionName();

    Object sendMessage(MessageContext messageContext);
}
