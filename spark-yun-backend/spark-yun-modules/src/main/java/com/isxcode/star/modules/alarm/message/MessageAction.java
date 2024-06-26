package com.isxcode.star.modules.alarm.message;

public interface MessageAction {

  String getActionName();

  void sendMessage(MessageContext messageContext);
}
