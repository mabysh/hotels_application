package com.demo.app.hotel.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CATEGORY")
public class Category extends AbstractEntity {

    @NotNull
    @Column(name = "NAME")
    private String name;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    public String getName() {
        return name;
   }

    public void setName(String name) {
        this.name = name;
   }

    public Category() { }

    public Category(String name) {
        this.name = name;
    }

    public int getVersion() { return version; }

    @Override
    public String toString() {
            return name + ", id is: " + getId();
    }

    @Override
    protected Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }

}
