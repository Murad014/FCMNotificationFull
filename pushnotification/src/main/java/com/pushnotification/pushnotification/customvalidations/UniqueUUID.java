package com.pushnotification.pushnotification.customvalidations;


import com.pushnotification.pushnotification.customvalidations.impl.UniqueUUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUUIDValidator.class)
public @interface UniqueUUID {
    String message() default "Already exist";
    boolean isUnique() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}