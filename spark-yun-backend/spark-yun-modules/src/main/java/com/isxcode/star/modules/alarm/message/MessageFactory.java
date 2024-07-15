package com.isxcode.star.modules.alarm.message;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageFactory {

    private final Map<String, MessageRunner> actionMap;

    public MessageFactory(ApplicationContext applicationContext) {

        actionMap = applicationContext.getBeansOfType(MessageRunner.class).values().stream()
            .collect(Collectors.toMap(MessageRunner::getActionName, action -> action));
    }

    public MessageRunner getMessageAction(String messageType) {

        return Optional.ofNullable(actionMap.get(messageType)).orElseThrow(() -> new RuntimeException("该通知方法不支持"));
    }
}
