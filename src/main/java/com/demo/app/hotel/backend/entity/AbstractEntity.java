package com.demo.app.hotel.backend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  //   @Version
   // private int version;

    public Long getId() { return id; }

//    @Column(name = "OPTLOCK")
 //   public int getVersion() { return version; }

    public boolean isPersisted() {
		return id != null;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(this.id == null) {
            return false;
        }

        if (obj instanceof Hotel && obj.getClass().equals(getClass())) {
            return this.id.equals(((Hotel) obj).getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
