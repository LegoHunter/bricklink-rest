package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SupersetEntry {
    private Integer color_id;                  // The ID of the color of the item
    private List<SupersetEntryDetail> entries;
}
