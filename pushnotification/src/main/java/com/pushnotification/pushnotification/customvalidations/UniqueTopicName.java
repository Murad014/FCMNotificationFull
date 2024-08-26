package com.pushnotification.pushnotification.customvalidations;

import com.pushnotification.pushnotification.customvalidations.impl.UniqueTopicNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTopicNameValidator.class)
public @interface UniqueTopicName {
    String message() default "Already exist";
    boolean isUnique() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}