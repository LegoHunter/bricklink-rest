package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {
    private Integer category_id; //	Integer	The ID of the category
    private String category_name; // The name of the category
    private Integer parent_id; // The ID of the parent category in category hierarchies ( 0 if this category is root )
}
