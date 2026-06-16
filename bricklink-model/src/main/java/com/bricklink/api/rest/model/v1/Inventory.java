package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Inventory {
    private Long inventory_id;      // The ID of the inventory
    private Item item;              // An object representation of the item
                                    //    item.no : Item's identification number in BrickLink catalog
                                    //    item.name : The name of this item
                                    //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
                                    //    item.category_id - The main category of the item
    private Integer color_id;       // The ID of the color of the item
    private String color_name;      // Color name of the item
    private Integer quantity;       // The number of items included in this inventory
    private String new_or_used;     // Indicates whether the item is new or used	N: New, U: Used
    private String completeness;    // Indicates whether the set is complete or incomplete (This value is valid only for SET type)	C: Complete, B: Incomplete, S: Sealed
    private Double unit_price;      // The original price of this item per sale unit
    private Integer bind_id;        // The ID of the parent lot that this lot is bound to
    private String description;     // A short description for this inventory
    private String remarks;         // User remarks on this inventory
    private Integer bulk;           // Buyers can buy this item only in multiples of the bulk amount
    private Boolean is_retain;      // Indicates whether the item retains in inventory after it is sold out
    private Boolean is_stock_room;  // Indicates whether the item appears only in owner’s inventory
    private String stock_room_id;   // Indicates the stockroom that the item to be placed when the user uses multiple stockroom	A, B, C
    private LocalDateTime date_created;   // The time this lot is created
    private Double my_cost;         // My Cost value to tracking the cost of item
    private Integer sale_rate;      // Sale value to adjust item price	Must be less than 100. 20 for 20% sale
    private Integer tier_quantity1; // A parameter for Tiered pricing	0 for no tier sale option
    private Integer tier_quantity2; // A parameter for Tiered pricing	0 for no tier sale option, Must be greater than tier_quantity1
    private Integer tier_quantity3; // A parameter for Tiered pricing	0 for no tier sale option, Must be greater than tier_quantity2
    private Double tier_price1;     // A parameter for Tiered pricing	0 for no tier sale option. Must be less than unit_price
    private Double tier_price2;     // A parameter for Tiered pricing	0 for no tier sale option, Must be less than tier_price1
    private Double tier_price3;     // A parameter for Tiered pricing	0 for no tier sale option, Must be less than tier_price2
    private Double my_weight;       // Custom weight of the item	Upcoming
}