package com.wanted.jaringoby.common.validations;

import com.wanted.jaringoby.common.exceptions.http.request.InvalidRequestInputException;
import com.wanted.jaringoby.common.exceptions.http.request.MissingRequestInputException;
import java.util.Set;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindingResultChecker {

    private final Set<String> missingFieldErrorCode = Set.of("NotBlank");
    private final Set<String> invalidFieldErrorCode = Set.of("Pattern");

    public void checkBindingErrors(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return;
        }

        FieldError fieldError = bindingResult.getFieldError();

        if (fieldError == null) {
            throw new RuntimeException();
        }

        String code = fieldError.getCode();

        if (code == null) {
            throw new RuntimeException();
        }

        if (isMissingFieldErrorCode(code)) {
            throw new MissingRequestInputException();
        }

        if (isInvalidFieldErrorCode(code)) {
            throw new InvalidRequestInputException();
        }

        throw new RuntimeException();
    }

    private boolean isMissingFieldErrorCode(String code) {
        return missingFieldErrorCode.contains(code);
    }

    private boolean isInvalidFieldErrorCode(String code) {
        return invalidFieldErrorCode.contains(code);
    }
}
