package com.hr.newwork.repositories;

import com.hr.newwork.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity.
 * Provides CRUD operations and custom queries for users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     * @param email the user's email
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users in a given department.
     * @param department the department name
     * @return a list of Users in the department
     */
    List<User> findByDepartment(String department);

    /**
     * Finds all users managed by a specific manager.
     * @param managerId the UUID of the manager
     * @return a list of Users managed by the manager
     */
    List<User> findByManager_Id(UUID managerId);

    /**
     * Finds all users in a department managed by a specific manager.
     * @param department the department name
     * @param managerId the UUID of the manager
     * @return a list of Users in the department and managed by the manager
     */
    List<User> findByDepartmentAndManager_Id(String department, UUID managerId);

    /**
     * Finds a user by their UUID (id).
     * @param id the UUID of the user
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findById(UUID id);

    /**
     * Finds all users with a specific role.
     * @param role the role name
     * @return a list of Users with the given role
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findByRole(String role);

    /**
     * Finds all users in a department with a specific role.
     * @param department the department name
     * @param role the role name
     * @return a list of Users in the department with the given role
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE u.department = :department AND r.name = :role")
    List<User> findByDepartmentAndRole(String department, String role);

    /**
     * Finds all users managed by a specific manager with a specific role.
     * @param managerId the UUID of the manager
     * @param role the role name
     * @return a list of Users managed by the manager with the given role
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE u.manager.id = :managerId AND r.name = :role")
    List<User> findByManager_IdAndRole(UUID managerId, String role);

    /**
     * Finds all users in a department managed by a specific manager with a specific role.
     * @param department the department name
     * @param managerId the UUID of the manager
     * @param role the role name
     * @return a list of Users in the department, managed by the manager, with the given role
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE u.department = :department AND u.manager.id = :managerId AND r.name = :role")
    List<User> findByDepartmentAndManager_IdAndRole(String department, UUID managerId, String role);
}
