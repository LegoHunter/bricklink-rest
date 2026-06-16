package com.bricklink.api.rest.model.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Address {
    private Name name;           //	Object	An object representation of a person's name
    private String full;         //	String	The full address in not-well-formatted
    private String address1;     //	String	The first line of the address	It is provided only if a buyer updated his/her address and name as a normalized form
    private String address2;     //	String	The second line of the address	It is provided only if a buyer updated his/her address and name as a normalized form
    private String country_code; //	String	The country code	ISO 3166-1 alpha-2 (exception: UK instead of GB)
    private String city;         //	String	The city	It is provided only if a buyer updated his/her address and name as a normalized form
    private String state;        //	String	The state	It is provided only if a buyer updated his/her address and name as a normalized form
    private String postal_code;  //	String	The postal code	It is provided only if a buyer updated his/her address and name as a normalized form
}
