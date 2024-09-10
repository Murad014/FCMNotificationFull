package com.pushnotification.pushnotification.helpers;

import com.pushnotification.pushnotification.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GenerateResponseHelper {
    private final MessageSourceReaderHelper messageSourceReaderHelper;

    @Autowired
    public GenerateResponseHelper(MessageSourceReaderHelper messageSourceReaderHelper) {
        this.messageSourceReaderHelper = messageSourceReaderHelper;
    }

    public <D>  ResponseDto<D> generateResponse(int code,
                                                String messageKey,
                                                D data,
                                                String path){

        String message = messageSourceReaderHelper.getMessage(messageKey);

        var responseDto = new ResponseDto<D>();
        responseDto.setCode(code);
        responseDto.setMessage(message);
        responseDto.setData(data);
        responseDto.setPath(path);

        return responseDto;
    }
}
