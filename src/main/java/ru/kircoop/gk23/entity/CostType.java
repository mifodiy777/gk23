package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Тип расходов
 */
@Entity
public class CostType implements Serializable {

    /**
     * Идентификатор типа расхода
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Наименование типа расхода
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CostType costType = (CostType) o;
        return Objects.equals(id, costType.id) && Objects.equals(name, costType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
