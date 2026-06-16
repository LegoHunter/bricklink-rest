package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Item {
    private String no;             // Item's identification number in BrickLink catalog
    private String name;           // The name of the item
    private String type;           // The type of the item	MINIFIG, PART, SET, BOOK, GEAR, CATALOG, INSTRUCTION, UNSORTED_LOT, ORIGINAL_BOX
    private Integer category_id;   // The main category of the item
    private String alternate_no;   // Alternate item number	Alternate item number
    private String image_url;      // Image link for this item
    private String thumbnail_url;  // Image thumbnail link for this item
    private Double weight;         // Point Number	The weight of the item in grams	with 2 decimal places
    private String dim_x;          // Length of the item	Item dimensions with 2 decimal places
    private String dim_y;          // Width of the item	Item dimensions with 2 decimal places
    private String dim_z;          // Height of the item	Item dimensions with 2 decimal places
    private Integer year_released; // Item year of release
    private String description;    // Short description for this item
    private Boolean is_obsolete;   // Indicates whether the item is obsolete
    private String language_code;  // Item language code	Item language
}
