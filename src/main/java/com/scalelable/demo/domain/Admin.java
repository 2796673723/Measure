package com.scalelable.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Admin {
    private int id;
    private String username;
    private String password;
    private String company;
}
