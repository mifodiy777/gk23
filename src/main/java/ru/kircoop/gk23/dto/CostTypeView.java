package ru.kircoop.gk23.dto;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Тип расходов
 */
@Data
public class CostTypeView implements Serializable {
    private Integer id;
    private String name;
}
