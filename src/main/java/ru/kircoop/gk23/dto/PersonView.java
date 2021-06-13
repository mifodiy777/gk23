package ru.kircoop.gk23.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PersonView implements Serializable {
    private Integer id;
    private String name;
    private String lastName;
    private String fatherName;
    private String telephone;
    private String benefits;
    private Boolean memberBoard;
    private String city;
    private String street;
    private String home;
    private String apartment;
}
