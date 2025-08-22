package com.nsl.operatorInterface.utility;

import java.io.Serializable;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "bigint")
    protected Long id;

    @Column(name = "ACTIVE", nullable = false, columnDefinition = "bit")
    protected boolean status;

    @Column(name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long databaseId) {
        this.id = databaseId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean active) {
        this.status = active;
    }

    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        BaseEntity that = (BaseEntity) other;
        return id != null && id.equals(that.id) &&
               status == that.status &&
               (modifiedOn != null ? modifiedOn.equals(that.modifiedOn) : that.modifiedOn == null) &&
               (createdOn != null ? createdOn.equals(that.createdOn) : that.createdOn == null);
    }

    @Override
    public final int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status ? 1 : 0);
        result = 31 * result + (modifiedOn != null ? modifiedOn.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        return result;
    }
}
