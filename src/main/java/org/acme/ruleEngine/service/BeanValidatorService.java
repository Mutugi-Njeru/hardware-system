package org.acme.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class BeanValidatorService {

    private final Validator validator;

    @Inject
    public BeanValidatorService(Validator validator) {
        this.validator = validator;
    }

    public List<String> validateDTO(Object dto) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            for (ConstraintViolation<Object> violation : violations) {
                errorMessages.add(violation.getMessage());
            }
            return errorMessages;
        } else {
            return Collections.emptyList(); // Return an empty list for successful validation
        }
    }
}
