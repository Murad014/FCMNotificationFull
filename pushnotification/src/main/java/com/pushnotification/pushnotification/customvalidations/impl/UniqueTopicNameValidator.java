package com.pushnotification.pushnotification.customvalidations.impl;

import com.google.storage.v2.StorageProto;
import com.pushnotification.pushnotification.customvalidations.UniqueToken;
import com.pushnotification.pushnotification.customvalidations.UniqueTopicName;
import com.pushnotification.pushnotification.repository.TopicRepository;
import com.pushnotification.pushnotification.repository.UserRepository;
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
        boolean existTopicName = topicRepository.existsByName(topicName);
        return !existTopicName;
    }
}
