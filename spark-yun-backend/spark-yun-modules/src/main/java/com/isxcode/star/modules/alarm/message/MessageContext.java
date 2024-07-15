package com.isxcode.star.modules.alarm.message;

import com.isxcode.star.api.alarm.dto.MessageConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageContext {

    private String email;

    private String phone;

    private String content;

    private String alarmId;

    private String alarmType;

    private String alarmEvent;

    private String msgId;

    private String receiver;

    private LocalDateTime sendDateTime;

    private MessageConfig messageConfig;

    private String tenantId;

    private String instanceId;
}
