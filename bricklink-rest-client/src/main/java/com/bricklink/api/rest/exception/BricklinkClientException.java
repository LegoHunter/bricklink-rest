package com.bricklink.api.rest.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public class BricklinkClientException extends RuntimeException {
    private final int code;
    private final String message;
    private final String description;

    @Override
    public String getMessage() {
        return MessageFormat.format("Bricklink Error Code=[{0}], Message=[{1}], Description=[{2}]", code, message, description);
    }
}