package com.example.apigateway.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
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
        var errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var errorCode = (int) errorAttributes.getOrDefault("status", 500);
        var errorMessage = switch (errorCode) {
            case 404 -> "The requested service route is undefined";
            case 503 -> "Service is temporarily unavailable";
            case 504 -> "The upstream service timed out";
            default -> "An unexpected internal error occurred";
        };

        return ServerResponse.status(errorCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Map.of(
                "errorCode", errorCode,
                "errorMessage", errorMessage,
                "path", request.path()
            )));
    }
}

