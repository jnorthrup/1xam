package com.gigabid;

import javax.persistence.Enumerated;
import javax.persistence.Entity;

/**
     * User: jim
 * Date: Aug 17, 2009
 * Time: 4:12:07 PM
 */                                       
 
public enum SchedulePolicy {
    weekdays,
    weekends,
    peak, offpeak
}
