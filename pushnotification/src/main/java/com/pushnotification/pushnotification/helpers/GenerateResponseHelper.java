package com.pushnotification.pushnotification.helpers;

import com.pushnotification.pushnotification.dto.ResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GenerateResponseHelper {
    private final MessageSource messageSource;

    public GenerateResponseHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public <D>  ResponseDto<D> generateResponse(int code,
                                                String message,
                                                D data,
                                                String path){

        message = messageSource.getMessage(
                message,
                null,
                LocaleContextHolder.getLocale());

        var responseDto = new ResponseDto<D>();

        responseDto.setCode(code);
        responseDto.setMessage(message);
        responseDto.setData(data);
        responseDto.setPath(path);

        return responseDto;
    }
}
