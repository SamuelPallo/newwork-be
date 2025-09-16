package com.hr.newwork.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String description;
}

