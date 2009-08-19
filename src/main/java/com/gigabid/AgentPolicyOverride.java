package com.gigabid;

import javax.persistence.*;

/**
 * User: jim
 * Date: Aug 17, 2009
 * Time: 11:33:57 PM
 */
@Entity
public class AgentPolicyOverride {
    @Id
    @GeneratedValue
    Long id;


    @ManyToOne
    Agent agent;
    @Enumerated
    AgentPolicy policy;

    String parameters;
}
