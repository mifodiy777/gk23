package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Сущность описывающая затраты, расходы
 */
@Entity
public class Cost implements Serializable {

    /**
     * Идентификатор расхода
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cost_generator")
    @SequenceGenerator(name="cost_generator", sequenceName = "cost_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Тип расхода
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_type")
    private CostType type;

    /**
     * Дополнительное описание расхода
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Сумма расхода
     */
    @Column(name = "money", nullable = false)
    private Long money;

    /**
     * Дата расхода
     */
    @OrderBy("date desc ")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CostType getType() {
        return type;
    }

    public void setType(CostType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cost cost = (Cost) o;
        return Objects.equals(id, cost.id) && Objects.equals(type, cost.type) && Objects.equals(money, cost.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, money);
    }
}
