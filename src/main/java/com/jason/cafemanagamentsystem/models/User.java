package com.jason.cafemanagamentsystem.models;

import java.io.Serializable;
import lombok.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;


@NamedQuery(name = "User.findByEmailId", query = "SELECT u FROM User u WHERE u.email = :email")

@NamedQuery(name = "User.getAllUser", query = "SELECT new com.jason.cafemanagamentsystem.wrapper.UserWrapper(u.id, u.name, u.email, u.phone, u.status) FROM User u WHERE u.role = 'USER'")

@NamedQuery(name = "User.getAllAdmin", query = "SELECT  u.email FROM User u WHERE u.role = 'ADMIN'")

@NamedQuery(name = "User.updateStatus", query = "UPDATE User u SET u.status = :status WHERE u.id = :id")

@Data
@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;



}
