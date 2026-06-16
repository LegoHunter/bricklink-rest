package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PriceGuide {
    private Item item;              // An object representation of the item
                                    //    item.no : Item's identification number in BrickLink catalog
                                    //    item.name : The name of this item
                                    //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
                                    //    item.category_id - The main category of the item
    private String new_or_used;     // Indicates whether the price guide is for new or used	N: New, U: Used
    private String currency_code;   // The currency code of the price
    private Double min_price;       // The lowest price of the item (in stock / that was sold for last 6 months )
    private Double max_price;       // The highest price of the item (in stock / that was sold for last 6 months )
    private Double avg_price;       // The average price of the item (in stock / that was sold for last 6 months )
    private Double qty_avg_price;   // The average price of the item (in stock / that was sold for last 6 months ) by quantity
    private Integer unit_quantity;  // The number of times the item has been sold for last 6 months; The number of inventories that include the item
    private Integer total_quantity; // The number of items has been sold for last 6 months; The total number of the items in stock
    private List<PriceDetail> price_detail; // A list of objects that represent the detailed information of the price
}
