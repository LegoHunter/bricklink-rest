package com.bricklink.api.rest.model.v1;

import com.bricklink.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class Shipping {
    private String method;              //	String	Shipping method name
    private String method_id;           //	String	Shipping method ID
    private String tracking_no;         //	String	Tracking numbers for the shipping
    private String tracking_link;       //	String	URL for tracking the shipping	API-only field. It is not shown on the BrickLink pages.
    @JsonDeserialize(using = DateUtils.ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = DateUtils.ZonedDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private ZonedDateTime date_shipped; //	Timestamp	Shipping date	API-only field. It is not shown on the BrickLink pages.
    private Address address;            //	Object	The object representation of the shipping address	API - Name data normalization

}
