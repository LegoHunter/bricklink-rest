package com.bricklink.api.rest.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PriceDetail {
    private Integer quantity;           // The number of the items in the inventory
    private Double unit_price;          // Point Number	The original price of this item per sale unit
    private String seller_country_code; // The country code of the seller's location	ISO 3166-1 alpha-2 (exception: UK instead of GB)
    private String buyer_country_code;  // The country code of the buyer's location	ISO 3166-1 alpha-2 (exception: UK instead of GB)
    private String date_ordered;        // The time the order was created
    private String shipping_available;  // Indicates whether or not the seller ships to your country(based on the user profile)

    @JsonIgnore
    private Double qunatity;            // To be deprecated. Typo.
}
