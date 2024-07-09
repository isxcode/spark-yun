package com.isxcode.star.modules.alarm.message;

import com.isxcode.star.api.alarm.constants.MessageType;
import org.springframework.stereotype.Service;

@Service
public class AliSmsMessage extends MessageRunner {

  @Override
  public String getActionName() {
    return MessageType.ALI_SMS;
  }

  @Override
  public void sendMessage(MessageContext messageContext) {
    System.out.println("发送阿里云短信");
  }
}
