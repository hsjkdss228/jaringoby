package com.wanted.jaringoby.common.config.ulid;

import com.wanted.jaringoby.common.utils.UlidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UlidGeneratorConfig {

    @Bean
    public UlidGenerator ulidGenerator() {
        return new UlidGenerator();
    }
}
