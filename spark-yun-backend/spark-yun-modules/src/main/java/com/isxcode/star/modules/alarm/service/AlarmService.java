package com.isxcode.star.modules.alarm.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.alarm.constants.AlarmSendStatus;
import com.isxcode.star.api.alarm.constants.AlarmStatus;
import com.isxcode.star.api.alarm.constants.AlarmType;
import com.isxcode.star.api.alarm.constants.MessageStatus;
import com.isxcode.star.api.alarm.dto.MessageConfig;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.AlarmInstanceEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageFactory;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmInstanceRepository;
import com.isxcode.star.modules.alarm.repository.AlarmRepository;
import com.isxcode.star.modules.alarm.repository.MessageRepository;
import com.isxcode.star.modules.user.service.UserService;
import com.isxcode.star.modules.work.entity.VipWorkVersionEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.VipWorkVersionRepository;
import com.isxcode.star.modules.work.service.WorkService;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowVersionEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowVersionRepository;
import com.isxcode.star.modules.workflow.service.WorkflowService;
import com.isxcode.star.security.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlarmService {

    private final MessageRepository messageRepository;

    private final AlarmRepository alarmRepository;

    private final MessageFactory messageFactory;

    private final UserService userService;

    private final AlarmInstanceRepository alarmInstanceRepository;

    private final WorkflowVersionRepository workflowVersionRepository;

    private final VipWorkVersionRepository vipWorkVersionRepository;

    private final WorkService workService;

    private final WorkflowService workflowService;

    public MessageEntity getMessage(String messageId) {

        return messageRepository.findById(messageId).orElseThrow(() -> new IsxAppException("消息体不存在"));
    }

    public String getMessageName(String messageId) {

        MessageEntity messageEntity = messageRepository.findById(messageId).orElse(null);
        return messageEntity == null ? messageId : messageEntity.getName();
    }

    public AlarmEntity getAlarm(String alarmId) {

        return alarmRepository.findById(alarmId).orElseThrow(() -> new IsxAppException("告警不存在"));
    }

    public String getAlarmName(String alarmId) {

        AlarmEntity alarmEntity = alarmRepository.findById(alarmId).orElse(null);
        return alarmEntity == null ? alarmId : alarmEntity.getName();
    }

    public AlarmInstanceEntity getAlarmInstance(String alarmInstanceId) {

        return alarmInstanceRepository.findById(alarmInstanceId).orElseThrow(() -> new IsxAppException("告警实例不存在"));
    }

    /**
     * 异步给定时作业发消息.
     */
    @Async
    public void sendWorkMessage(WorkInstanceEntity workInstance, String alarmEvent) {

        TENANT_ID.set(workInstance.getTenantId());

        Optional<VipWorkVersionEntity> byId = vipWorkVersionRepository.findById(workInstance.getVersionId());
        VipWorkVersionEntity workVersionEntity = byId.get();

        // 当配置了告警处理
        if (!Strings.isEmpty(workVersionEntity.getAlarmList())) {

            // 遍历多个告警
            JSON.parseArray(workVersionEntity.getAlarmList(), String.class).forEach(alarmId -> {

                // 查询告警信息
                AlarmEntity alarm = getAlarm(alarmId);

                // 告警类型不对，默认跳过
                if (!AlarmType.WORK.equals(alarm.getAlarmType())) {
                    return;
                }

                // 如果警告没开启，默认跳过
                if (!AlarmStatus.ENABLE.equals(alarm.getStatus())) {
                    return;
                }

                // 满足当前事件当发送
                if (alarmEvent.equals(alarm.getAlarmEvent())) {

                    // 获取环境参数
                    Map<String, String> valueMap = getWorkAlarmValueMap(workInstance, workVersionEntity);

                    // 拼接消息内容
                    final String[] content = {alarm.getAlarmTemplate()};
                    valueMap.forEach((k, v) -> content[0] = content[0].replace(k, v));

                    // 获取告警中当消息体
                    if (!Strings.isEmpty(alarm.getMsgId())) {

                        // 构建消息发送的context
                        MessageEntity message = getMessage(alarm.getMsgId());
                        MessageConfig messageConfig = JSON.parseObject(message.getMsgConfig(), MessageConfig.class);

                        // 构建消息发送执行期
                        MessageRunner messageRunner = messageFactory.getMessageAction(message.getMsgType());
                        MessageContext messageContext = MessageContext.builder().alarmType(alarm.getAlarmType())
                            .alarmId(alarmId).alarmEvent(alarmEvent).msgId(alarm.getMsgId())
                            .messageConfig(messageConfig).tenantId(message.getTenantId()).content(content[0])
                            .instanceId(workInstance.getId()).build();

                        // 获取需要发送的人
                        List<String> receiverList = JSON.parseArray(alarm.getReceiverList(), String.class);

                        // 查询联系人的信息，发送消息
                        receiverList.forEach(userId -> {

                            // 发送时间
                            UserEntity user = userService.getUser(userId);
                            messageContext.setEmail(user.getEmail());
                            messageContext.setPhone(user.getPhone());
                            messageContext.setReceiver(userId);
                            messageContext.setSendDateTime(LocalDateTime.now());

                            // 如果消息体不为可用状态,不发消息
                            if (!MessageStatus.ACTIVE.equals(message.getStatus())) {
                                AlarmInstanceEntity alarmInstanceEntity = AlarmInstanceEntity.builder()
                                    .alarmId(messageContext.getAlarmId()).alarmType(messageContext.getAlarmType())
                                    .alarmEvent(messageContext.getAlarmEvent()).msgId(alarm.getMsgId())
                                    .content(messageContext.getContent()).receiver(messageContext.getReceiver())
                                    .instanceId(messageContext.getInstanceId())
                                    .sendDateTime(messageContext.getSendDateTime()).build();
                                alarmInstanceEntity.setSendStatus(AlarmSendStatus.FAIL);
                                alarmInstanceEntity.setResponse("消息体不为激活状态");
                                alarmInstanceRepository.save(alarmInstanceEntity);
                            } else {
                                messageRunner.send(messageContext);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 异步给定时作业流发消息.
     */
    @Async
    public void sendWorkflowMessage(WorkflowInstanceEntity workflowInstance, String alarmEvent) {

        TENANT_ID.set(workflowInstance.getTenantId());

        Optional<WorkflowVersionEntity> byId = workflowVersionRepository.findById(workflowInstance.getVersionId());
        WorkflowVersionEntity workflowVersionEntity = byId.get();

        // 当配置了告警处理
        if (!Strings.isEmpty(workflowVersionEntity.getAlarmList())) {

            // 遍历多个告警
            JSON.parseArray(workflowVersionEntity.getAlarmList(), String.class).forEach(alarmId -> {

                // 查询告警信息
                AlarmEntity alarm = getAlarm(alarmId);

                // 告警类型不对，默认跳过
                if (!AlarmType.WORKFLOW.equals(alarm.getAlarmType())) {
                    return;
                }

                // 如果警告没开启，默认跳过
                if (!AlarmStatus.ENABLE.equals(alarm.getStatus())) {
                    return;
                }

                // 满足当前事件当发送
                if (alarmEvent.equals(alarm.getAlarmEvent())) {

                    // 拼接消息内容
                    Map<String, String> valueMap = getWorkflowAlarmValueMap(workflowInstance, workflowVersionEntity);
                    final String[] content = {alarm.getAlarmTemplate()};
                    valueMap.forEach((k, v) -> content[0] = content[0].replace(k, v));

                    // 获取需要发送的人
                    List<String> receiverList = JSON.parseArray(alarm.getReceiverList(), String.class);

                    // 获取告警中当消息体
                    if (!Strings.isEmpty(alarm.getMsgId())) {

                        // 构建消息发送的context
                        MessageEntity message = getMessage(alarm.getMsgId());
                        MessageConfig messageConfig = JSON.parseObject(message.getMsgConfig(), MessageConfig.class);

                        // 构建消息发送执行期
                        MessageRunner messageRunner = messageFactory.getMessageAction(message.getMsgType());
                        MessageContext messageContext = MessageContext.builder().alarmType(alarm.getAlarmType())
                            .alarmId(alarmId).alarmEvent(alarmEvent).msgId(alarm.getMsgId())
                            .messageConfig(messageConfig).tenantId(message.getTenantId()).content(content[0])
                            .instanceId(workflowInstance.getId()).build();

                        // 查询联系人的信息，发送消息
                        receiverList.forEach(userId -> {

                            // 发送时间
                            UserEntity user = userService.getUser(userId);
                            messageContext.setEmail(user.getEmail());
                            messageContext.setPhone(user.getPhone());
                            messageContext.setReceiver(userId);
                            messageContext.setSendDateTime(LocalDateTime.now());

                            // 如果消息体不为可用状态,不发消息
                            if (!MessageStatus.ACTIVE.equals(message.getStatus())) {
                                AlarmInstanceEntity alarmInstanceEntity = AlarmInstanceEntity.builder()
                                    .alarmId(messageContext.getAlarmId()).alarmType(messageContext.getAlarmType())
                                    .alarmEvent(messageContext.getAlarmEvent()).msgId(alarm.getMsgId())
                                    .content(messageContext.getContent()).receiver(messageContext.getReceiver())
                                    .instanceId(messageContext.getInstanceId())
                                    .sendDateTime(messageContext.getSendDateTime()).build();
                                alarmInstanceEntity.setSendStatus(AlarmSendStatus.FAIL);
                                alarmInstanceEntity.setResponse("消息体不为激活状态");
                                alarmInstanceRepository.save(alarmInstanceEntity);
                            } else {
                                messageRunner.send(messageContext);
                            }
                        });
                    }
                }
            });
        }
    }

    public String getCurrentDateTime() {

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getCurrentDate() {

        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Map<String, String> getWorkAlarmValueMap(WorkInstanceEntity workInstance, VipWorkVersionEntity workVersion) {

        WorkEntity work = workService.getWorkEntity(workVersion.getWorkId());
        WorkflowEntity workflow = workflowService.getWorkflow(work.getWorkflowId());

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("${qing.work_name}", work.getName());
        valueMap.put("${qing.work_id}", work.getId());
        valueMap.put("${qing.workflow_name}", workflow.getName());
        valueMap.put("${qing.workflow_id}", workflow.getId());
        valueMap.put("${qing.work_instance_id}", workInstance.getId());
        valueMap.put("${qing.workflow_instance_id}", workInstance.getWorkflowInstanceId());
        valueMap.put("${qing.current_datetime}", getCurrentDateTime());
        valueMap.put("${qing.current_date}", getCurrentDate());
        return valueMap;
    }

    public Map<String, String> getWorkflowAlarmValueMap(WorkflowInstanceEntity workflowInstance,
        WorkflowVersionEntity workflowVersion) {

        WorkflowEntity workflow = workflowService.getWorkflow(workflowVersion.getWorkflowId());

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("${qing.workflow_name}", workflow.getName());
        valueMap.put("${qing.workflow_id}", workflow.getId());
        valueMap.put("${qing.workflow_instance_id}", workflowInstance.getId());
        valueMap.put("${qing.current_datetime}", getCurrentDateTime());
        valueMap.put("${qing.current_date}", getCurrentDate());
        return valueMap;
    }
}
