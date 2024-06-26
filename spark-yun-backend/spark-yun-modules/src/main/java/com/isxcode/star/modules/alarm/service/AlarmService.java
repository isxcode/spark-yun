package com.isxcode.star.modules.alarm.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.alarm.dto.MessageConfig;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageFactory;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmRepository;
import com.isxcode.star.modules.alarm.repository.MessageRepository;
import com.isxcode.star.modules.user.service.UserService;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.security.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmService {

	private final MessageRepository messageRepository;

	private final AlarmRepository alarmRepository;

	private final MessageFactory messageFactory;

	private final UserService userService;

	public MessageEntity getMessage(String messageId) {

		return messageRepository.findById(messageId).orElseThrow(() -> new IsxAppException("消息体不存在"));
	}

	public AlarmEntity getAlarm(String alarmId) {

		return alarmRepository.findById(alarmId).orElseThrow(() -> new IsxAppException("告警不存在"));
	}

	/**
	 * 异步给定时作业发消息.
	 */
	@Async
	public void sendWorkMessage(WorkRunContext workRunContext, WorkInstanceEntity workInstance, String alarmEvent) {

		// 当配置了告警处理
		if (workRunContext.getAlarmList() != null && !workRunContext.getAlarmList().isEmpty()) {

			// 遍历多个告警
			workRunContext.getAlarmList().forEach(alarmId -> {

				// 查询告警信息
				AlarmEntity alarm = getAlarm(alarmId);

				// 满足当前事件当发送
				if (alarmEvent.equals(alarm.getAlarmEvent())) {

					// 拼接消息内容
					String content = alarm.getAlarmTemplate();

					// 获取需要发送的人
					List<String> receiverList = JSON.parseArray(alarm.getReceiverList(), String.class);

					// 获取告警中当消息体
					if (alarm.getMsgList() != null && !alarm.getMsgList().isEmpty()) {

						// 遍历消息体，逐个发送
						List<String> msgIdList = JSON.parseArray(alarm.getMsgList(), String.class);
						msgIdList.forEach(msgId -> {

							// 构建消息发送的context
							MessageEntity message = getMessage(msgId);
							MessageConfig messageConfig = JSON.parseObject(message.getMsgConfig(), MessageConfig.class);
							MessageRunner messageAction = messageFactory.getMessageAction(message.getMsgType());
							MessageContext messageContext = MessageContext.builder().alarmType(alarm.getAlarmType())
									.alarmId(alarmId).alarmEvent(alarmEvent).msgType(message.getMsgType())
									.messageConfig(messageConfig).tenantId(message.getTenantId()).content(content)
									.build();

							// 查询联系人的信息，发送消息
							receiverList.forEach(userId -> {

								// 发送时间
								UserEntity user = userService.getUser(userId);
								messageContext.setEmail(user.getEmail());
								messageContext.setReceiver(userId);
								messageContext.setSendDateTime(LocalDateTime.now());
								messageAction.send(messageContext);
							});
						});
					}
				}
			});
		}
	}
}