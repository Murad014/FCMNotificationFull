package com.pushnotification.pushnotification.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.customvalidations.UniqueTopicName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class TopicRequestDto {
    @Valid
    @NotNull(message="Topic name cannot be null")
    @NotBlank(message="Topic name cannot be empty")
    @UniqueTopicName(message="{topic.exists}")
    String name;

    @Valid
    @NotNull(message="Topic description cannot be null")
    Map<PlatformLanguages, String> description;


}