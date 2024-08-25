package com.pushnotification.pushnotification.helpers;


import com.pushnotification.pushnotification.dto.ResponseDto;
import org.springframework.stereotype.Component;

@Component
public class GeneratorHelper {
    public <D> ResponseDto<D> createResponse(String message, D data, int code, String path){
        var responseDto = new ResponseDto<D>();

        responseDto.setMessage(message);
        responseDto.setData(data);
        responseDto.setCode(code);
        responseDto.setPath(path);

        return responseDto;
    }

}
