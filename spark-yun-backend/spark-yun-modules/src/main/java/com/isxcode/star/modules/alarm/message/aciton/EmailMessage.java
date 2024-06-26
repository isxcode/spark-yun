package com.isxcode.star.modules.alarm.message.aciton;

import com.isxcode.star.api.alarm.constants.MessageType;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import org.springframework.stereotype.Service;

@Service
public class EmailMessage extends MessageRunner {

	@Override
	public String getActionName() {
		return MessageType.EMAIL;
	}

	@Override
	public void sendMessage(MessageContext messageContext) {
		System.out.println("发送邮箱");
	}
}
