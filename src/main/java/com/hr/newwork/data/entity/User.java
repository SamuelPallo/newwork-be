package com.hr.newwork.data.entity;

import com.hr.newwork.util.SensitiveDataConverter;
import com.hr.newwork.util.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    private boolean isActive;
    private LocalDate hireDate;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = SensitiveDataConverter.class)
    private SensitiveData sensitiveData;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Relationships
    @OneToMany(mappedBy = "manager")
    private List<User> subordinates = new ArrayList<>();

}


