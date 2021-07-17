package ru.kircoop.gk23.dto;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Data
public class ContributionView implements Serializable {
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
}
