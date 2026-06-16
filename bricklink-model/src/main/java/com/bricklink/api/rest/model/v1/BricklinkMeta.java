package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BricklinkMeta {
    private Integer code;        // API result code. (2xx if successful, any other number otherwise)
    private String message;     // More granular information about the result
    private String description; // Detailed description about the result
}
