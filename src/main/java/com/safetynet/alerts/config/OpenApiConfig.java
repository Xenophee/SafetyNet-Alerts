package com.safetynet.alerts.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            String methodName = handlerMethod.getMethod().getName();
            if (methodName.equals("create")) {
                operation.getResponses().remove("404");
            }
            if (!methodName.equals("create")) {
                operation.getResponses().remove("409");
            }
            operation.getResponses().remove("500");
            return operation;
        };
    }
}
