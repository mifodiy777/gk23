package ru.kircoop.gk23.dto;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
public class RentView implements Serializable {
    private Integer id;
    private int yearRent;
    private int contributeMax;
    private int contLandMax;
    private int contTargetMax;
}
