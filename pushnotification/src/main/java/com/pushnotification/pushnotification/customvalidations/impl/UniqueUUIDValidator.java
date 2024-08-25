package com.pushnotification.pushnotification.customvalidations.impl;


import com.pushnotification.pushnotification.customvalidations.UniqueUUID;
import com.pushnotification.pushnotification.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUUIDValidator implements ConstraintValidator<UniqueUUID, String> {

    private final UserRepository userRepository;

    public UniqueUUIDValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueUUID constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext context) {
        boolean existUUID = userRepository.existsByUuid(uuid);
        return !existUUID;
    }
}
