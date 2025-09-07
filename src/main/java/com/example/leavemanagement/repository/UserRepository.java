package com.example.leavemanagement.repository;

import com.example.leavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByManager(User manager);

    @Query("SELECT u FROM User u WHERE u.isHod = true")
    List<User> findHeadsOfDepartment();

    @Query("SELECT u FROM User u WHERE u.isHr = true")
    List<User> findHrPersonnel();

    @Query("SELECT u FROM User u WHERE u.manager = :manager")
    List<User> findByManagerId(@Param("manager") User manager);
}