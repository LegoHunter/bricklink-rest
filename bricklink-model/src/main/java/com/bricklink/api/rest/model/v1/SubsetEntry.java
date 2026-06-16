package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SubsetEntry {
    private Integer match_no;                // A identification number given to a matching group that consists of regular items and alternate items.	0 if there is no matching of alternative item
    private List<SubsetEntryDetail> entries; // A list of the items included in the specified item
}
