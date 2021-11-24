package com.scalelable.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Manager {
    private Integer id;
    private String username;
    private String mobile;
    private Integer contractorId;
    private Integer projectId;
}
