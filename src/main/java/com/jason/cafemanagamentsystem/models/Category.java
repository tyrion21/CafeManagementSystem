package com.jason.cafemanagamentsystem.models;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "category")

// @NamedQuery(name = "Category.getAllCategory", query = "SELECT c FROM Category c where c.id in (select p.category from Product p where p.status = 'true')")
@NamedQuery(name = "Category.getAllCategory", query = "SELECT c FROM Category c where c in (select p.category from Product p where p.status = 'true')")

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
}
