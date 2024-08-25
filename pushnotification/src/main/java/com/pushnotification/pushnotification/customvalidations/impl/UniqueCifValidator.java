package com.pushnotification.pushnotification.customvalidations.impl;


import com.pushnotification.pushnotification.customvalidations.UniqueCif;
import com.pushnotification.pushnotification.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueCifValidator implements ConstraintValidator<UniqueCif, String> {

    private final UserRepository userRepository;

    public UniqueCifValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueCif constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cif, ConstraintValidatorContext context) {
        boolean existCif = userRepository.existsByCif(cif);
        return !existCif;
    }
}
