package com.gigabid;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

@Entity
public class Login
{
    @Transient
    @PersistenceContext(unitName = "gigabid-jpa")
    private static EntityManager entityManager;

    @Id
    private long id;

    private String name;

    public static Login find(long id)
    {
        return entityManager.find(Login.class, new Long(id));
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static EntityManager getEntityManager()
    {
        return entityManager;
    }

    public static void setEntityManager(EntityManager entityManager)
    {
        Login.entityManager = entityManager;
    }
}
