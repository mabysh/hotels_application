package com.demo.app.hotel;

import java.io.Serializable;

public class Category implements Serializable, Cloneable{

    private String name;

    private Long id;

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    protected Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category that = (Category) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public Category() { }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
   }

   public void setName(String name) {
        this.name = name;
   }

   public void setId(Long id) { this.id = id; }

   public Long getId() { return id; }

}
