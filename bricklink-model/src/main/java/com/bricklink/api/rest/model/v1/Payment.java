package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Payment {
    private String method;           //	String	The payment method for this order
    private String currency_code;    //	String	Currency code of the payment	ISO 4217
    private LocalDateTime date_paid; //	Timestamp	The time the buyer paid
    private String status;           //	String	Payment status	Available status
}
