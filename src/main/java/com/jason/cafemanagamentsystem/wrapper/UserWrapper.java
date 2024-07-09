package com.jason.cafemanagamentsystem.wrapper;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
public class UserWrapper {

    private Integer id;

    private String name;

    private String email;

    private String phone;

    private String status;

    public UserWrapper(Integer id, String name, String email, String phone, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
}
