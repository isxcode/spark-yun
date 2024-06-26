package com.isxcode.star.modules.alarm.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.alarm.constants.AlarmStatus;
import com.isxcode.star.api.alarm.constants.MessageStatus;
import com.isxcode.star.api.alarm.req.*;
import com.isxcode.star.api.alarm.res.PageAlarmRes;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.mapper.AlarmMapper;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageFactory;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmRepository;
import com.isxcode.star.modules.alarm.repository.MessageRepository;
import com.isxcode.star.modules.user.service.UserService;
import com.isxcode.star.security.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmBizService {

	private final MessageRepository messageRepository;

	private final AlarmRepository alarmRepository;

	private final AlarmService alarmService;

	private final AlarmMapper alarmMapper;

	private final MessageFactory messageFactory;

	private final UserService userService;

	public void addMessage(AddMessageReq addMessageReq) {

		MessageEntity messageEntity = alarmMapper.addMessageReqToMessageEntity(addMessageReq);
		messageEntity.setMsgConfig(JSON.toJSONString(addMessageReq.getMessageConfig()));
		messageEntity.setStatus(MessageStatus.NEW);
		messageRepository.save(messageEntity);
	}

	public void updateMessage(UpdateMessageReq updateMessageReq) {

		MessageEntity message = alarmService.getMessage(updateMessageReq.getId());
		message.setMsgConfig(JSON.toJSONString(updateMessageReq.getMessageConfig()));
		message.setName(updateMessageReq.getName());
		message.setRemark(updateMessageReq.getRemark());
		message.setMsgType(updateMessageReq.getMsgType());
		message.setStatus(MessageStatus.UN_CHECK);
		messageRepository.save(message);
	}

	public Page<PageMessageRes> pageMessage(PageMessageReq pageMessageReq) {

		Page<MessageEntity> messageEntities = messageRepository.searchAll(pageMessageReq.getSearchKeyWord(),
				PageRequest.of(pageMessageReq.getPage(), pageMessageReq.getPageSize()));

		return messageEntities.map(alarmMapper::messageEntityToPageMessageRes);
	}

	public void deleteMessage(DeleteMessageReq deleteMessageReq) {

		MessageEntity message = alarmService.getMessage(deleteMessageReq.getId());
		messageRepository.delete(message);
	}

	public void enableMessage(EnableMessageReq enableMessageReq) {

		MessageEntity message = alarmService.getMessage(enableMessageReq.getId());
		message.setStatus(MessageStatus.ACTIVE);
		messageRepository.save(message);
	}

	public void disableMessage(DisableMessageReq disableMessageReq) {

		MessageEntity message = alarmService.getMessage(disableMessageReq.getId());
		message.setStatus(MessageStatus.DISABLE);
		messageRepository.save(message);
	}

	public void checkMessage(CheckMessageReq checkMessageReq) {

		MessageEntity message = alarmService.getMessage(checkMessageReq.getId());

		MessageRunner messageAction = messageFactory.getMessageAction(message.getMsgType());

		UserEntity user = userService.getUser(checkMessageReq.getUserId());

		messageAction
				.send(MessageContext.builder().content(checkMessageReq.getContent()).email(user.getEmail()).build());
	}

	public void addAlarm(AddAlarmReq addAlarmReq) {

		AlarmEntity alarm = alarmMapper.addAlarmReqToAlarmEntity(addAlarmReq);
		alarm.setReceiverList(JSON.toJSONString(addAlarmReq.getReceiverList()));
		alarm.setMsgList(JSON.toJSONString(addAlarmReq.getMsgList()));
		alarm.setStatus(AlarmStatus.DISABLE);
		alarmRepository.save(alarm);
	}

	public void updateAlarm(UpdateAlarmReq updateAlarmReq) {

		AlarmEntity alarm = alarmService.getAlarm(updateAlarmReq.getId());
		alarm.setName(updateAlarmReq.getName());
		alarm.setRemark(updateAlarmReq.getRemark());
		alarm.setAlarmType(updateAlarmReq.getAlarmType());
		alarm.setAlarmEvent(updateAlarmReq.getAlarmEvent());
		alarm.setReceiverList(JSON.toJSONString(updateAlarmReq.getReceiverList()));
		alarm.setMsgList(JSON.toJSONString(updateAlarmReq.getMsgList()));
		alarmRepository.save(alarm);
	}

	public Page<PageAlarmRes> pageAlarm(PageAlarmReq pageAlarmReq) {

		Page<AlarmEntity> alarmEntities = alarmRepository.searchAll(pageAlarmReq.getSearchKeyWord(),
				PageRequest.of(pageAlarmReq.getPage(), pageAlarmReq.getPageSize()));

		return alarmEntities.map(alarmMapper::alarmEntityToPageAlarmRes);
	}

	public void deleteAlarm(DeleteAlarmReq deleteAlarmReq) {

		AlarmEntity alarm = alarmService.getAlarm(deleteAlarmReq.getId());
		alarmRepository.delete(alarm);
	}

	public void enableAlarm(EnableAlarmReq enableAlarmReq) {

		AlarmEntity alarm = alarmService.getAlarm(enableAlarmReq.getId());
		alarm.setStatus(AlarmStatus.ENABLE);
		alarmRepository.save(alarm);
	}

	public void disableAlarm(DisableAlarmReq disableAlarmReq) {

		AlarmEntity alarm = alarmService.getAlarm(disableAlarmReq.getId());
		alarm.setStatus(AlarmStatus.DISABLE);
		alarmRepository.save(alarm);
	}

}
