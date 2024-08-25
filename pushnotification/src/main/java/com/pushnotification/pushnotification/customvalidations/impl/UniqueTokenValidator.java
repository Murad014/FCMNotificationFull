package com.pushnotification.pushnotification.customvalidations.impl;


import com.pushnotification.pushnotification.customvalidations.UniqueToken;
import com.pushnotification.pushnotification.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueTokenValidator implements ConstraintValidator<UniqueToken, String> {

    private final UserRepository userRepository;

    public UniqueTokenValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueToken constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String token, ConstraintValidatorContext context) {
        boolean existToken = userRepository.existsByToken(token);
        return !existToken;
    }
}
