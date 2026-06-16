package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SupersetEntryDetail {
    private Item item;        // An object representation of the item
                              //    item.no : Item's identification number in BrickLink catalog
                              //    item.name : The name of this item
                              //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
                              //    item.category_id - The main category of the item
    private Integer quantity; // Indicates that how many specified items are included in this super item
    private String appear_as; // Indicates how an entry in an inventory appears as
}
