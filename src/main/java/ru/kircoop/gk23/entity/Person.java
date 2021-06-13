package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_person", nullable = false)
    private Integer id;

    @Column(name = "name_person")
    private String name;

    @Column(name = "lastname_person")
    private String lastName;

    @Column(name = "fathername_person")
    private String fatherName;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "benefits")
    private String benefits;

    @Column(name = "member_board")
    private Boolean memberBoard;

    //Город
    @Column(name = "city")
    private String city;

    //Улица
    @Column(name = "street")
    private String street;

    //Дом
    @Column(name = "home")
    private String home;

    //Квартира - необязательный параметр
    @Column(name = "apartment")
    private String apartment;

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public Boolean getMemberBoard() {
        return memberBoard;
    }

    public void setMemberBoard(Boolean memberBoard) {
        this.memberBoard = memberBoard;
    }

    public String getFIO() {
        return this.lastName + " " + this.name + " " + this.fatherName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getAddr() {
        StringBuilder addr = new StringBuilder();
        addr.append("г.");
        addr.append(this.city);
        addr.append(" ул.");
        addr.append(this.street);
        addr.append(" д.");
        addr.append(this.home);
        if (!this.apartment.isEmpty()) {
            addr.append(" кв.");
            addr.append(this.apartment);
        }
        return addr.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
