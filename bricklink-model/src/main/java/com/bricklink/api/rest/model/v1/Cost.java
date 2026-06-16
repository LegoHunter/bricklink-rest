package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Cost {
    private String currency_code; //	String	The display currency code of the user	ISO 4217
    private Double subtotal;      //	Fixed Point Number	The subtotal price in DISPLAY currency
    private Double grand_total;   //	Fixed Point Number	The grand total price in DISPLAY currency
    private Double etc1;          //	Fixed Point Number	Extra charge for this order (tax, packing, etc.) in DISPLAY currency
    private Double etc2;          //	Fixed Point Number	Extra charge for this order (tax, packing, etc.) in DISPLAY currency
    private Double insurance;     //	Fixed Point Number	Insurance cost in DISPLAY currency
    private Double shipping;      //	Fixed Point Number	Shipping cost in DISPLAY currency
    private Double credit;        //	Fixed Point Number	Credit applied to this order in DISPLAY currency
    private Double coupon;        //	Fixed Point Number	Amount of coupon discount in DISPLAY currency
    private Double vat_rate;      //	Fixed Point Number	VAT percentage applied to this order	Upcoming Feature
    private Double vat_amount;    //	Fixed Point Number	Total amount of VAT included in the grand_total price in DISPLAY currency	Upcoming Feature
    private Double salesTax_collected_by_BL; // salesTax_collected_by_BL
}
