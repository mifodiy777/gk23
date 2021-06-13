package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.util.Objects;

/*Класс суммы взноса*/
@Entity
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_rent", nullable = false)
    private Integer id;

    //Год начисления
    @Column(name = "year_rent", nullable = false, unique = true)
    @OrderBy("yearRent DESC")
    private int yearRent;

    //Начисление членского взноса
    @Column(name = "contribute_rent")
    private int contributeMax;


    //Начисление аренды земли
    @Column(name = "cont_land_rent")
    private int contLandMax;

    //Начисление целевого взноса
    @Column(name = "cont_target_rent")
    private int contTargetMax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getYearRent() {
        return yearRent;
    }

    public void setYearRent(int yearRent) {
        this.yearRent = yearRent;
    }

    public int getContributeMax() {
        return contributeMax;
    }

    public void setContributeMax(int contributeMax) {
        this.contributeMax = contributeMax;
    }

    public int getContLandMax() {
        return contLandMax;
    }

    public void setContLandMax(int contLandMax) {
        this.contLandMax = contLandMax;
    }

    public int getContTargetMax() {
        return contTargetMax;
    }

    public void setContTargetMax(int contTargetMax) {
        this.contTargetMax = contTargetMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rent rent = (Rent) o;
        return yearRent == rent.yearRent && Objects.equals(id, rent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, yearRent);
    }
}
