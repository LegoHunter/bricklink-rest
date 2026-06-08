package com.bricklink.api.rest.exception;

import lombok.Getter;

@Getter
public class BricklinkServerException extends RuntimeException {
    private final int status;

    public BricklinkServerException(int status, String message) {
        super(message);
        this.status = status;
    }
}
