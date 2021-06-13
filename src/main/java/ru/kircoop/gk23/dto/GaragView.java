package ru.kircoop.gk23.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GaragView implements Serializable {
    private Integer id;
    private String series;
    private String number;
    private PersonView person;
    private String additionalInformation;
    private int oldContribute;


}
