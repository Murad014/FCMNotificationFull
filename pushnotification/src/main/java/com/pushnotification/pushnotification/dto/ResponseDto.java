package com.pushnotification.pushnotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ResponseDto<D> {
    int code;
    String message;
    D data;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String path;
}
