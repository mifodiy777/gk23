package ru.kircoop.gk23.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GaragView implements Serializable {
    private Integer id;
    private String series;
    private String number;
    private String fullName;
    private PersonView person;
    private String additionalInformation;
    private int oldContribute;
}
