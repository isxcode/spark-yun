package com.isxcode.star.modules.alarm.mapper;

import com.isxcode.star.api.alarm.req.AddAlarmReq;
import com.isxcode.star.api.alarm.req.AddMessageReq;
import com.isxcode.star.api.alarm.res.PageAlarmInstanceRes;
import com.isxcode.star.api.alarm.res.PageAlarmRes;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.AlarmInstanceEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
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
