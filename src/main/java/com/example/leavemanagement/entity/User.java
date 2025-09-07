package com.example.leavemanagement.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "manager")
    private List<User> subordinates = new ArrayList<>();

    @Column(name = "is_hod", nullable = false)
    private Boolean isHod = false;

    @Column(name = "is_hr", nullable = false)
    private Boolean isHr = false;

    @Column(name = "leave_balance", nullable = false)
    private Integer leaveBalance = 20;

    // Constructors, getters, and setters
    public User() {}

    public User(String username, String password, String firstName, String lastName,
                String email, List<String> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    // Getters and setters for all fields
    public Long getId()
    {
        return id;
    }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public List<User> getSubordinates() { return subordinates; }
    public void setSubordinates(List<User> subordinates) { this.subordinates = subordinates; }

    public Boolean getIsHod() { return isHod; }
    public void setIsHod(Boolean isHod) { this.isHod = isHod; }

    public Boolean getIsHr() { return isHr; }
    public void setIsHr(Boolean isHr) { this.isHr = isHr; }

    public Integer getLeaveBalance() { return leaveBalance; }
    public void setLeaveBalance(Integer leaveBalance) { this.leaveBalance = leaveBalance; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}