package ru.kircoop.gk23.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContributionView {

    private Integer id;
    private Integer year;
    private int contribute;
    private int contLand;
    private int contTarget;
    private int fines;
    private boolean finesOn;
    private LocalDate finesLastUpdate;
    private boolean benefitsOn;
    private boolean memberBoardOn;
    private int sum;
}
