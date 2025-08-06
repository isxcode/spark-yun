package com.isxcode.spark.modules.alarm.mapper;

import com.isxcode.spark.api.alarm.req.AddAlarmReq;
import com.isxcode.spark.api.alarm.req.AddMessageReq;
import com.isxcode.spark.api.alarm.res.PageAlarmInstanceRes;
import com.isxcode.spark.api.alarm.res.PageAlarmRes;
import com.isxcode.spark.api.alarm.res.PageMessageRes;
import com.isxcode.spark.modules.alarm.entity.AlarmEntity;
import com.isxcode.spark.modules.alarm.entity.AlarmInstanceEntity;
import com.isxcode.spark.modules.alarm.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlarmMapper {

    MessageEntity addMessageReqToMessageEntity(AddMessageReq addMessageReq);

    PageMessageRes messageEntityToPageMessageRes(MessageEntity messageEntity);

    @Mapping(target = "receiverList", ignore = true)
    AlarmEntity addAlarmReqToAlarmEntity(AddAlarmReq addAlarmReq);

    PageAlarmRes alarmEntityToPageAlarmRes(AlarmEntity alarmEntity);

    PageAlarmInstanceRes alarmInstanceEntityToPageAlarmInstanceRes(AlarmInstanceEntity alarmInstanceEntity);
}
