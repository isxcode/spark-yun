package com.isxcode.star.modules.alarm.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.alarm.constants.MessageStatus;
import com.isxcode.star.api.alarm.req.*;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.mapper.AlarmMapper;
import com.isxcode.star.modules.alarm.message.MessageAction;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageFactory;
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

		MessageAction messageAction = messageFactory.getMessageAction(message.getMsgType());

		UserEntity user = userService.getUser(checkMessageReq.getUserId());

		messageAction.sendMessage(
				MessageContext.builder().content(checkMessageReq.getContent()).email(user.getEmail()).build());
	}

}
