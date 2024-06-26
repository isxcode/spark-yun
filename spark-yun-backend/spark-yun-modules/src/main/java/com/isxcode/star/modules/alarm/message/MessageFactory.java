package com.isxcode.star.modules.alarm.message;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageFactory {

	private final Map<String, MessageAction> actionMap;

	public MessageFactory(ApplicationContext applicationContext) {

		actionMap = applicationContext.getBeansOfType(MessageAction.class).values().stream()
				.collect(Collectors.toMap(MessageAction::getActionName, action -> action));
	}

	public MessageAction getMessageAction(String messageType) {

		return Optional.ofNullable(actionMap.get(messageType)).orElseThrow(() -> new IsxAppException("该通知方法不支持"));
	}
}
