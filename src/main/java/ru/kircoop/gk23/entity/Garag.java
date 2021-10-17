package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Garag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_garag", nullable = false)
    private Integer id;

    //Ряд
    @Column(name = "series", nullable = false)
    private String series;

    //Номер
    @Column(name = "number", nullable = false)
    @OrderBy("number")
    private String number;

    //Владелец
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "id_person")
    private Person person;

    @Column(name = "additionalInformation")
    private String additionalInformation;

    //Периоды платежные - долги
    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("year ASC")
    private List<Contribution> contributions;

    //Долги прошлых лет не облагаемые пенями
    @Column(name = "old_contribute", nullable = false)
    private int oldContribute;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;

    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public int getOldContribute() {
        return oldContribute;
    }

    public void setOldContribute(int oldContribute) {
        this.oldContribute = oldContribute;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getName() {
        return this.series + "-" + this.number;
    }

    public String getFullName() {
        return " Ряд: " + this.series + " Номер: " + this.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Garag garag = (Garag) o;
        return Objects.equals(series, garag.series) && Objects.equals(number, garag.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, number);
    }
}
