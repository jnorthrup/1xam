package com.gigabid;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: jim
 * Date: Aug 17, 2009
 * Time: 4:16:23 PM
 */
@Entity
public class Geolocation {
    @Id
    @GeneratedValue
    Long id;

    Float lat, lon;

    String name;
    
}
