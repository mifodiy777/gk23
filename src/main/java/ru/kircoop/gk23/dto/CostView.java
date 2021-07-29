package ru.kircoop.gk23.dto;

import lombok.Data;
import ru.kircoop.gk23.entity.CostType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность описывающая затраты, расходы
 */
@Data
public class CostView implements Serializable {
    private Integer id;
    private String typeName;
    private Integer typeId;
    private String description;
    private Long money;
    private String date;
}
