package com.pushnotification.pushnotification.customvalidations.impl;

import com.pushnotification.pushnotification.constant.PlatformLanguages;
import com.pushnotification.pushnotification.customvalidations.UniqueTopicName;
import com.pushnotification.pushnotification.repository.TopicRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueTopicNameValidator implements ConstraintValidator<UniqueTopicName, String> {

    private final TopicRepository topicRepository;

    public UniqueTopicNameValidator(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public void initialize(UniqueTopicName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String topicName, ConstraintValidatorContext context) {
        boolean existTopicName = false;
        for(var lang: PlatformLanguages.values())
            existTopicName |= topicRepository.existsByName(topicName.toUpperCase().concat("_" + lang.toString()));

        return !existTopicName;
    }
}
