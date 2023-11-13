package com.wanted.jaringoby.common.config.validation;

import com.wanted.jaringoby.common.validations.BindingResultChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public BindingResultChecker bindingResultChecker() {
        return new BindingResultChecker();
    }
}
