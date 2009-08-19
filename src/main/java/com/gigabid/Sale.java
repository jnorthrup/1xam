package com.gigabid;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * User: jim
 * Date: Aug 17, 2009
 * Time: 11:32:20 PM
 */
@Entity
public class Sale {
    @Id
    @GeneratedValue
    Long id;
    
    @ManyToOne
    Account buyer;

    @ManyToOne
    Auction item;

    /**
     * time of Sale 
     */
    Date close;

    /**
     * the price at time of sale
      */
    Float closingPrice;
    /**
     * the number of units purchased at time of sale
     */
    Float unitsPurchased;

    @ManyToMany
    Set<Blob> blobReservations;
}
