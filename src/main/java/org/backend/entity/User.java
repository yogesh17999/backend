package org.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;
    private String password;
    private boolean locked;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private UserRoles role;
    private String phoneNumber;

//    @OneToMany
//    @JoinColumn(name = "parent_user_id")
//    private List<User> childUser;
}
