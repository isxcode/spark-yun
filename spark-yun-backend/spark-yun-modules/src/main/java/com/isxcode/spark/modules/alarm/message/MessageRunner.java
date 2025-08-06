package com.isxcode.spark.modules.alarm.message;

import com.isxcode.spark.api.alarm.constants.AlarmSendStatus;
import com.isxcode.spark.modules.alarm.entity.AlarmInstanceEntity;
import com.isxcode.spark.modules.alarm.repository.AlarmInstanceRepository;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;

public abstract class MessageRunner implements MessageAction {

    private final AlarmInstanceRepository alarmInstanceRepository;

    protected MessageRunner(AlarmInstanceRepository alarmInstanceRepository) {
        this.alarmInstanceRepository = alarmInstanceRepository;
    }

    public void send(MessageContext messageContext) {

        TENANT_ID.set(messageContext.getTenantId());

        try {
            Object sendResponse = sendMessage(messageContext);
            // 发送成功，写入实例
            AlarmInstanceEntity alarmInstanceEntity = messageContextToAlarmInstanceEntity(messageContext);
            alarmInstanceEntity.setSendStatus(AlarmSendStatus.SUCCESS);
            alarmInstanceEntity.setResponse(String.valueOf(sendResponse));
            alarmInstanceRepository.save(alarmInstanceEntity);
        } catch (Exception e) {

            // 发送失败，写入实例
            AlarmInstanceEntity alarmInstanceEntity = messageContextToAlarmInstanceEntity(messageContext);
            alarmInstanceEntity.setSendStatus(AlarmSendStatus.FAIL);
            alarmInstanceEntity.setResponse(String.valueOf(e.getMessage()));
            alarmInstanceRepository.save(alarmInstanceEntity);
        }
    }

    protected AlarmInstanceEntity messageContextToAlarmInstanceEntity(MessageContext messageContext) {

        return AlarmInstanceEntity.builder().alarmId(messageContext.getAlarmId())
            .alarmType(messageContext.getAlarmType()).alarmEvent(messageContext.getAlarmEvent())
            .msgId(messageContext.getMsgId()).content(messageContext.getContent())
            .instanceId(messageContext.getInstanceId()).receiver(messageContext.getReceiver())
            .sendDateTime(messageContext.getSendDateTime()).build();
    }
}
