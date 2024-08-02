package com.isxcode.star.modules.alarm.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.alarm.constants.AlarmSendStatus;
import com.isxcode.star.api.alarm.constants.AlarmStatus;
import com.isxcode.star.api.alarm.constants.MessageStatus;
import com.isxcode.star.api.alarm.dto.MessageConfig;
import com.isxcode.star.api.alarm.req.*;
import com.isxcode.star.api.alarm.res.CheckMessageRes;
import com.isxcode.star.api.alarm.res.PageAlarmInstanceRes;
import com.isxcode.star.api.alarm.res.PageAlarmRes;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.AlarmInstanceEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.mapper.AlarmMapper;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageFactory;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmInstanceRepository;
import com.isxcode.star.modules.alarm.repository.AlarmRepository;
import com.isxcode.star.modules.alarm.repository.MessageRepository;
import com.isxcode.star.modules.user.mapper.UserMapper;
import com.isxcode.star.modules.user.service.UserService;
import com.isxcode.star.security.user.UserEntity;
import com.isxcode.star.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final AlarmInstanceRepository alarmInstanceRepository;

    public void addMessage(AddMessageReq addMessageReq) {

        // 名称不能重复
        Optional<MessageEntity> byName = messageRepository.findByName(addMessageReq.getName());
        if (byName.isPresent()) {
            throw new IsxAppException("消息体名称重复");
        }

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

        Page<PageMessageRes> result = messageEntities.map(alarmMapper::messageEntityToPageMessageRes);

        // 翻译创建人名称
        result.getContent().forEach(e -> {
            e.setCreateByUsername(userService.getUserName(e.getCreateBy()));
            MessageConfig messageConfig = JSON.parseObject(e.getMsgConfig(), MessageConfig.class);
            messageConfig.setAccessKeySecret("");
            messageConfig.setPassword("");
            e.setMessageConfig(messageConfig);
        });
        return result;
    }

    public void deleteMessage(DeleteMessageReq deleteMessageReq) {

        MessageEntity message = alarmService.getMessage(deleteMessageReq.getId());
        messageRepository.delete(message);
    }

    public void enableMessage(EnableMessageReq enableMessageReq) {

        MessageEntity message = alarmService.getMessage(enableMessageReq.getId());

        // 状态必须是检测成功才能启动
        if (!MessageStatus.CHECK_SUCCESS.equals(message.getStatus())
            && !MessageStatus.DISABLE.equals(message.getStatus())) {
            throw new IsxAppException("检测通过后，才可以启用");
        }

        message.setStatus(MessageStatus.ACTIVE);
        messageRepository.save(message);
    }

    public void disableMessage(DisableMessageReq disableMessageReq) {

        MessageEntity message = alarmService.getMessage(disableMessageReq.getId());
        message.setStatus(MessageStatus.DISABLE);
        messageRepository.save(message);
    }

    public CheckMessageRes checkMessage(CheckMessageReq checkMessageReq) {

        MessageEntity message = alarmService.getMessage(checkMessageReq.getId());
        MessageConfig messageConfig = JSON.parseObject(message.getMsgConfig(), MessageConfig.class);
        MessageContext messageContext =
            MessageContext.builder().messageConfig(messageConfig).content(checkMessageReq.getContent()).build();

        UserEntity user = userService.getUser(checkMessageReq.getReceiver());
        messageContext.setEmail(user.getEmail());
        messageContext.setPhone(user.getPhone());

        try {
            MessageRunner messageAction = messageFactory.getMessageAction(message.getMsgType());
            messageAction.sendMessage(messageContext);
            message.setStatus(MessageStatus.CHECK_SUCCESS);
            message.setResponse(null);
            messageRepository.save(message);
            return CheckMessageRes.builder().checkStatus(AlarmSendStatus.SUCCESS).log("检测成功").build();
        } catch (Exception e) {
            message.setStatus(MessageStatus.CHECK_FAIL);
            message.setResponse(e.getMessage());
            messageRepository.save(message);
            return CheckMessageRes.builder().checkStatus(AlarmSendStatus.FAIL).log(e.getMessage()).build();
        }
    }

    public void addAlarm(AddAlarmReq addAlarmReq) {

        // 名称不能重复
        Optional<AlarmEntity> byName = alarmRepository.findByName(addAlarmReq.getName());
        if (byName.isPresent()) {
            throw new IsxAppException("名称不能重复");
        }

        AlarmEntity alarm = alarmMapper.addAlarmReqToAlarmEntity(addAlarmReq);
        alarm.setReceiverList(JSON.toJSONString(addAlarmReq.getReceiverList()));
        alarm.setStatus(AlarmStatus.ENABLE);
        alarmRepository.save(alarm);
    }

    public void updateAlarm(UpdateAlarmReq updateAlarmReq) {

        AlarmEntity alarm = alarmService.getAlarm(updateAlarmReq.getId());
        alarm.setName(updateAlarmReq.getName());
        alarm.setRemark(updateAlarmReq.getRemark());
        alarm.setAlarmType(updateAlarmReq.getAlarmType());
        alarm.setAlarmEvent(updateAlarmReq.getAlarmEvent());
        alarm.setAlarmTemplate(updateAlarmReq.getAlarmTemplate());
        alarm.setReceiverList(JSON.toJSONString(updateAlarmReq.getReceiverList()));
        alarm.setMsgId(updateAlarmReq.getMsgId());
        alarmRepository.save(alarm);
    }

    public Page<PageAlarmRes> pageAlarm(PageAlarmReq pageAlarmReq) {

        Page<AlarmEntity> alarmEntities = alarmRepository.searchAll(pageAlarmReq.getSearchKeyWord(),
            PageRequest.of(pageAlarmReq.getPage(), pageAlarmReq.getPageSize()));

        Page<PageAlarmRes> result = alarmEntities.map(alarmMapper::alarmEntityToPageAlarmRes);

        // 翻译消息体
        result.getContent().forEach(e -> {
            e.setMsgName(alarmService.getMessageName(e.getMsgId()));
            e.setCreateByUsername(userService.getUserName(e.getCreateBy()));
            List<String> receiverList = JSON.parseArray(e.getReceiverList(), String.class);
            List<UserEntity> receiverUsers = userRepository.findAllById(receiverList);
            e.setReceiverUsers(userMapper.userEntityToUserInfo(receiverUsers));
        });

        return result;
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

    public Page<PageAlarmInstanceRes> pageAlarmInstance(PageAlarmInstanceReq pageAlarmInstanceReq) {

        Page<AlarmInstanceEntity> alarmInstanceEntities =
            alarmInstanceRepository.searchAll(pageAlarmInstanceReq.getSearchKeyWord(),
                PageRequest.of(pageAlarmInstanceReq.getPage(), pageAlarmInstanceReq.getPageSize()));

        Page<PageAlarmInstanceRes> result =
            alarmInstanceEntities.map(alarmMapper::alarmInstanceEntityToPageAlarmInstanceRes);

        // 翻译告警名称
        result.getContent().forEach(e -> {
            e.setAlarmName(alarmService.getAlarmName(e.getAlarmId()));
            e.setMsgName(alarmService.getMessageName(e.getMsgId()));
            e.setReceiverUsername(userService.getUser(e.getReceiver()).getUsername());
        });

        return result;
    }

    public void deleteAlarmInstance(DeleteAlarmInstanceReq deleteAlarmInstanceReq) {

        AlarmInstanceEntity alarmInstance = alarmService.getAlarmInstance(deleteAlarmInstanceReq.getId());
        alarmInstanceRepository.delete(alarmInstance);
    }

}
