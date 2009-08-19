package com.gigabid;

import javax.persistence.*;
import java.util.Date;

/**
 * Dutch Auction Item  -- Dutch Auctions are auctions on a timer;  as the time passes the price cheapens.
 * <p/>
 * User: jim
 * Date: Aug 17, 2009
 * Time: 4:06:58 PM
 */
@Entity
public class Auction {
    @Id
    @GeneratedValue
    Long id;
    /**
     * Bandwidth is served from this agent.
     */
    @ManyToOne
    Agent agent;

    /**
     * auction creation time
     */
    Date created;
    /**
     * product becomes active if owned, at this instant
     */
    Date start;
    /**
     * product ceases to be available for sale or use at this instant
     */
    Date expire;

    /**
     * product purchased on or before start time costs this much "fresh"
     */
    Float initialBid;
    /**
     * product may cease to decline in price even before the product exits the event threshold of viabilty
     */
    Float minimumBid;
    /**
     * this defines how many gigs are for sale total
     */
    Float totalUnits;

    /**
     * this defines the smallest unit of bidding
     */
    Float incrementSize;
    /**
     * this defines the minimum number of increments
     */
    Float minIncrements;

    /**
     * product is active by policy of seller choice: Day/night, weekend/weekday
     */
    @Enumerated
    SchedulePolicy schedulePolicy;
    /**
     * defines the amount of bandwidth unit sold in this Auction 
     */
    @Enumerated
    PipeUnit unit;

}

