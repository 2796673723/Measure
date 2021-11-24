package com.scalelable.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Project {
    private Integer id;
    private String name;
    private Float start;
    private Float end;
    private Integer companyId;
    private Float progress;
}
