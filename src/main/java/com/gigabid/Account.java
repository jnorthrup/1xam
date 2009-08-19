package com.gigabid;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * User: jim
 * Date: Aug 17, 2009
 * Time: 4:10:38 PM
 */
@Entity
public class Account {
    @GeneratedValue
    @Id
    Long id;
    String name;
    String email;
    @OneToMany
    Set<Agent> agents;
    @Enumerated
    AgentPolicy defaultPolicy;
    @ManyToMany
    List<AgentPolicyOverride> overrides;
}
