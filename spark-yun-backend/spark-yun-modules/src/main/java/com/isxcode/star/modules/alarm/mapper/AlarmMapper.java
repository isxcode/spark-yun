package com.isxcode.star.modules.alarm.mapper;

import com.isxcode.star.api.alarm.req.AddMessageReq;
import com.isxcode.star.api.alarm.res.PageMessageRes;
import com.isxcode.star.modules.alarm.entity.AlarmEntity;
import com.isxcode.star.modules.alarm.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlarmMapper {

  MessageEntity addMessageReqToMessageEntity(AddMessageReq addMessageReq);

  PageMessageRes messageEntityToPageMessageRes(MessageEntity messageEntity);
}
