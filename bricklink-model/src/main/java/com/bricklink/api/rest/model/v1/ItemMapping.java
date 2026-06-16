package com.bricklink.api.rest.model.v1;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class ItemMapping {
    private Item item;              // An object representation of the item
                                    //    item.no : Item's identification number in BrickLink catalog
                                    //    item.type : The type of the item - MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
    private Integer color_id;       // Color ID of the item
    private String color_name;      // Color name of the item
    private String element_id;      // Element ID of the item in specific color
}



