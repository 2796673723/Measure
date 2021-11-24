package com.scalelable.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Process {

    public Process() {
    }

    public Process(Float start, Float end) {
        this.start = start;
        this.end = end;
    }

    private Integer id;
    private Float start;
    private Float end;
    private Integer type;
    private Integer projectId;
    private String date;
}
