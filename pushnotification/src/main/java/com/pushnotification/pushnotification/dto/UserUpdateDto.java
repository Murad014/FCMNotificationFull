package com.pushnotification.pushnotification.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateDto {
    @Valid
    @NotBlank
    @NotNull
    String token;

    @Valid
    @NotNull(message = "{NotNull.platform}")
    Platform platform;

    @Valid
    @NotNull(message = "{NotNull.platform_language}")
    PlatformLanguages platformLanguage;

    Set<String> topics;

}
