package com.isxcode.star.modules.alarm.message;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;

public abstract class MessageRunner implements MessageAction {

	public void send(MessageContext messageContext) {

		System.out.println("开始发送通知");
		try {
			sendMessage(messageContext);
		} catch (Exception e) {
			System.out.println("发送异常");
			throw new IsxAppException("运行异常，" + e.getMessage());
		}
		System.out.println("结束发送通知");
	}

}
