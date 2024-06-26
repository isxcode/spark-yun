package com.isxcode.star.modules.alarm.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmService {

	private final MessageRepository messageRepository;

	public MessageEntity getMessage(String messageId) {

		return messageRepository.findById(messageId).orElseThrow(() -> new IsxAppException("消息体不存在"));
	}
}
