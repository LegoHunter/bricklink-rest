package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubsetEntryDetail {
    private Item item;              // An object representation of the item
                                    //    item.no : Item's identification number in BrickLink catalog
                                    //    item.name : The name of this item
                                    //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
                                    //    item.category_id - The main category of the item
    private Integer color_id;       // The ID of the color of the item
    private Integer quantity;       // The number of items that are included in
    private Integer extra_quantity; // The number of items that are appear as "extra" item
    private String is_alternate;    // Indicates that the item is appear as "alternate" item in this specified item
}
