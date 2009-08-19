package com.gigabid;

import javax.persistence.*;
import java.util.Set;

/**
 * User: jim
 * Date: Aug 17, 2009
 * Time: 4:11:33 PM
 */
@Entity
public class Agent {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    Geolocation region;
    @ManyToOne
    Account owner;

    @ManyToMany
    Set<Blob> blobs;
    String name;
    String description;

}
