package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Name {
    private String full;    //	String	The full name of this person, including middle names, suffixes, etc.
    private String first;   //	String	The family name (last name) of this person	It is provided only if a buyer updated his/her address and name as a normalized form
    private String last;    //	String	The given name (first name) of this person	It is provided only if a buyer updated his/her address and name as a normalized form
}
