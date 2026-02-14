package com.example.apigateway.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
@Order(-2)
public class GlobalGatewayExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalGatewayExceptionHandler(ErrorAttributes errorAttributes,
                                         WebProperties webProperties,
                                         ApplicationContext applicationContext,
                                         ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        int status = (int) error.getOrDefault("status", 500);

        // Custom user-friendly message based on status
        String userMessage = switch (status) {
            case 404 -> "The requested service route is undefined.";
            case 503 -> "Service is temporarily unavailable. Please try again later.";
            case 504 -> "The upstream service timed out.";
            default -> "An unexpected internal error occurred.";
        };

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Map.of(
                "status", status,
                "message", userMessage,
                "path", request.path()
            )));
    }
}

