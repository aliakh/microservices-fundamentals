package com.microservices.storageservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "storages")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String storageType;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        var that = (Storage) obj;
        return Objects.equals(getId(), that.getId())
            && Objects.equals(getStorageType(), that.getStorageType())
            && Objects.equals(getBucket(), that.getBucket())
            && Objects.equals(getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStorageType(), getBucket(), getPath());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Storage.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("storageType='" + storageType + "'")
            .add("bucket='" + bucket + "'")
            .add("path='" + path + "'")
            .toString();
    }
}
