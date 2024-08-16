package com.bricklink.api.rest.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @EqualsAndHashCode.Include
    private String order_id;                   //String	Unique identifier for this order for internal use
    private LocalDateTime date_ordered;        //Timestamp	The time the order was created
    private LocalDateTime date_status_changed; //	Timestamp	The time the order status was last modified
    private String seller_name;                //	String	The username of the seller in BL
    private String store_name;                 //	String	The store name displayed on BL store pages
    private String buyer_name;                 //	String	The username of the buyer in BL
    private String buyer_email;                //	String	E-mail address of the buyer
    private Integer buyer_order_count;         //	Integer	Total count of all orders placed by the buyer in the seller's store. Includes the order just placed and also purged orders
    private Boolean require_insurance;         //	Boolean	Indicates whether the buyer requests insurance for this order
    private String status;                     //	String	The status of an order	Available status
    private Boolean is_invoiced;               //	Boolean	Indicates whether the order invoiced
    private Boolean is_filed;                  //	Boolean	Indicates whether the order is filed
    private Boolean sent_drive_thru;           //	Boolean	Indicates whether "Thank You, Drive Thru!" email has been sent
    private String remarks;                    //	String	User remarks for this order
    private Integer total_count;               //	Integer	The total number of items included in this order
    private Integer unique_count;              //	Integer	The unique number of items included in this order
    private Double total_weight;               //	Fixed Point Number	The total weight of the items ordered
                                               //       It applies the seller's custom weight when present to override the catalog weight
                                               //       0 if the order includes at least one item without any weight information or incomplete set
    private Payment payment;                   //	Object	Information related to the payment of this order
    private Shipping shipping;                 //	Object	Information related to the shipping
    private Cost cost;                         //	Object  Cost information for this order
    private Cost disp_cost;                    //	Object	Cost information for this order in DISPLAY currency

    @JsonIgnore
    public boolean isPaid() {
        return OrderStatus.valueOf(this.getStatus()).compareTo(OrderStatus.PAID) >= 0;
    }

    @JsonIgnore
    public boolean isShipped() {
        return OrderStatus.valueOf(this.getStatus()).compareTo(OrderStatus.SHIPPED) >= 0;
    }

    @JsonIgnore
    public boolean isCancelled() {
        return OrderStatus.valueOf(this.getStatus()).compareTo(OrderStatus.CANCELLED) == 0;
    }

    @JsonIgnore
    public boolean isNotShipped() {
        return !isShipped();
    }
}