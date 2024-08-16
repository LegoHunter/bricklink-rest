package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItem {
    private Long inventory_id;              // The ID of the inventory
    private Item item;                      // An object representation of the item
                                            //    item.no : Item's identification number in BrickLink catalog
                                            //    item.name : The name of this item
                                            //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
                                            //    item.category_id - The main category of the item
    private Integer color_id;               // The ID of the color of the item
    private String color_name;              // Color name of the item
    private Integer quantity;               // The number of items included in this inventory
    private String new_or_used;             // Indicates whether the item is new or used	N: New, U: Used
    private String completeness;            // Indicates whether the set is complete or incomplete (This value is valid only for SET type)	C: Complete, B: Incomplete, S: Sealed
    private Double unit_price;              // The original price of this item per sale unit
    private Double unit_price_final;        // The unit price of this item after applying tiered pricing policy

    private Double disp_unit_price;         // The original price of this item per sale unit
    private Double disp_unit_price_final;   // The unit price of this item after applying tiered pricing policy

    private String currency_code;           //	String	The currency code of the price	ISO 4217
    private String disp_currency_code;;     //	String	The display currency code of the user	ISO 4217

    private String remarks;         // User remarks on this inventory
    private String description;     // A short description for this inventory
    private Double weight;          // The weight of the item that overrides the catalog weight
}