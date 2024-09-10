package com.pushnotification.pushnotification.exceptions;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.helpers.MessageSourceReaderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalAPIException {

    private final MessageSourceReaderHelper messageSourceReaderHelper;

    private final static String WRONG_INPUT_ERROR_MESSAGE = "wrong.input.error";
    private final static String UNKNOWN_ERROR_MESSAGE = "unknown.error";
    private final static String HTTP_MESSAGE_NOT_READABLE_MESSAGE = "http.message.not.readable.error";
    private final static String RESOURCE_NOT_FOUND_MESSAGE = "resource.not.found.error";
    @Autowired
    public GlobalAPIException(MessageSourceReaderHelper messageSourceReaderHelper) {
        this.messageSourceReaderHelper = messageSourceReaderHelper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        var response = new ResponseDto<Map<String, String>>();
        response.setPath(getLocation().getPath());
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(messageSourceReaderHelper.getMessage(WRONG_INPUT_ERROR_MESSAGE));
        response.setData(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongRequestBodyException.class)
    public  ResponseEntity<ResponseDto<Object>> handleWrongRequestBodyException(WrongRequestBodyException exception) {
        var responseDto = new ResponseDto<>();
        responseDto.setPath(getLocation().getPath());
        responseDto.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        responseDto.setMessage(exception.getMessage());

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        var responseDto = new ResponseDto<>();
        responseDto.setPath(getLocation().getPath());
        responseDto.setCode(HttpStatus.NOT_FOUND.value());
        responseDto.setMessage(
                String.format(messageSourceReaderHelper.getMessage(RESOURCE_NOT_FOUND_MESSAGE),
                        exception.getResourceName(), exception.getFieldName(), exception.getFieldValue())
        );

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Object>> handleInvalidFormatException(HttpMessageNotReadableException exception) {
        var responseDto = new ResponseDto<>();
        responseDto.setPath(getLocation().getPath());
        responseDto.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        responseDto.setMessage(messageSourceReaderHelper.getMessage(HTTP_MESSAGE_NOT_READABLE_MESSAGE));

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> unknownError(Exception exception) {
        var responseDto = new ResponseDto<>();
        responseDto.setPath(getLocation().getPath());
        responseDto.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseDto.setMessage(messageSourceReaderHelper.getMessage(UNKNOWN_ERROR_MESSAGE));

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private URI getLocation(){
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri();
    }

}
