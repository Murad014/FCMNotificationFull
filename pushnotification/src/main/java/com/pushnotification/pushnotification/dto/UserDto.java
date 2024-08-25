package com.pushnotification.pushnotification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pushnotification.pushnotification.constant.Platform;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.customvalidations.UniqueCif;
import com.pushnotification.pushnotification.customvalidations.UniqueToken;
import com.pushnotification.pushnotification.customvalidations.UniqueUUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class UserDto {
    @Valid
            @Size(min = 7, max = 7, message = "{Size.cif}")
            @NotBlank
            @NotNull
            @UniqueCif
    String cif;

    @Valid
            @NotBlank
            @NotNull
            @UniqueUUID
    String uuid;

    @Valid
            @NotBlank
            @NotNull
            @UniqueToken
    String token;

    @Valid
            @NotNull(message = "{NotNull.platform}")
    Platform platform;

    Set<String> topics;

    @Valid
            @NotNull(message = "{NotNull.platform_language}")
    PlatformLanguages platformLanguage;

}
