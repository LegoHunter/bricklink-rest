package com.bricklink.api.rest.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public class BricklinkClientException extends RuntimeException {
    private final int code;
    private final String message;
    private final String description;

    /** The BrickLink error code: the meta code of the response envelope, or the HTTP status. */
    public int getCode() {
        return code;
    }

    /** True for RESOURCE_NOT_FOUND (404), for example a lot that does not exist or was deleted. */
    public boolean isNotFound() {
        return code == 404;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Bricklink Error Code=[{0}], Message=[{1}], Description=[{2}]", code, message, description);
    }
}