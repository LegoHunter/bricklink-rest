package com.bricklink.api.rest.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class OrderItemBatch {
    @JsonProperty("")
    private List<OrderItem> orderItems;
}
