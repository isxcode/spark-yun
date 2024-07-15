package com.isxcode.star.modules.alarm.message;

public interface MessageAction {

    String getActionName();

    Object sendMessage(MessageContext messageContext);
}
