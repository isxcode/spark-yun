package com.isxcode.star.api.alarm.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAlarmInstanceRes {

    private String id;

    private String alarmId;

    private String alarmName;

    private String sendStatus;

    private String alarmType;

    private String alarmEvent;

    private String msgId;

    private String msgName;

    private String content;

    private String receiver;

    private String receiverUsername;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sendDateTime;

    private String instanceId;

    private String response;
}
