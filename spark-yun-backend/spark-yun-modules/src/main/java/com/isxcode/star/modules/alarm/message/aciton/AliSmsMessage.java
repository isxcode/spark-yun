package com.isxcode.star.modules.alarm.message.aciton;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.isxcode.star.api.alarm.constants.MessageType;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmInstanceRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
public class AliSmsMessage extends MessageRunner {

    protected AliSmsMessage(AlarmInstanceRepository alarmInstanceRepository) {
        super(alarmInstanceRepository);
    }

    @Override
    public String getActionName() {
        return MessageType.ALI_SMS;
    }

    @Override
    public Object sendMessage(MessageContext messageContext) {

        if (Strings.isEmpty(messageContext.getPhone())) {
            throw new RuntimeException("用户手机号为空");
        }

        try {
            Config config = new Config().setAccessKeyId(messageContext.getMessageConfig().getAccessKeyId())
                .setAccessKeySecret(messageContext.getMessageConfig().getAccessKeySecret())
                .setRegionId(messageContext.getMessageConfig().getRegion()).setEndpoint("dysmsapi.aliyuncs.com");

            Client client = new Client(config);
            SendSmsRequest sendSmsRequest =
                new SendSmsRequest().setSignName(messageContext.getMessageConfig().getSignName())
                    .setTemplateCode(messageContext.getMessageConfig().getTemplateCode())
                    .setPhoneNumbers(messageContext.getPhone()).setTemplateParam(messageContext.getContent());

            RuntimeOptions runtime = new RuntimeOptions();
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            if (!"OK".equals(sendSmsResponse.getBody().getCode())) {
                throw new RuntimeException(sendSmsResponse.getBody().getMessage());
            }
            return JSON.toJSONString(sendSmsResponse);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            throw new RuntimeException(Common.assertAsString(error.message));
        }
    }
}
