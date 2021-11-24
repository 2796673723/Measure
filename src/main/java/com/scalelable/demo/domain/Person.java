package com.scalelable.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Person {
    private Integer id;
    private String username;
    private String password;
    private String mobile;
    private Integer managerId;
}
