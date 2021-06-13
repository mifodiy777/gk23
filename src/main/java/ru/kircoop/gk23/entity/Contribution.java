package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

@Entity
public class Contribution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_count", nullable = false)
    private Integer id;

    //Год платежного периода
    @OrderBy
    @Column(name = "year", nullable = false)
    private Integer year;


    //Членский взнос - долг
    /* При появлении нового начисления и периода данное значения
           становиться равным значением членского взноса класса Rent */
    @Column(name = "contribute")
    private int contribute;

    //Аренда земли - долг
    /* При появлении нового начисления и периода данное значения
           становиться равным значением арендой земли класса Rent */
    @Column(name = "contLand")
    private int contLand;

    //Целевой взнос - долг
    /* При появлении нового начисления и периода данное значения
           становиться равным значением целевого взноса класса Rent */
    @Column(name = "contTarget")
    private int contTarget;

    //Пени - долг
    /* Данное значение будет увеличиваться на 0,1% от суммы начисления (Класс Rent этого же периода )
       с 1 июля данного периода. При частичной оплате в данном периоде счетчик останавливаеться.
       При полной оплате долга за этот период в последующие года счетчик так же остановиться.
       При достижении данного значения суммы взноса за период - счетчик остановиться.
       При полной оплате данное значение становиться 0  */
    @Column(name = "fines")
    private int fines;

    //Включение начисление пеней
    //Включаеться при не полностью погашеном долге в след. году
    //Если true то начисление пеней включено
    @Column(name = "fines_on")
    private boolean finesOn;

    //Дата последнего обновления пеней
    @Column(name = "fines_last_update")
    private Calendar finesLastUpdate;

    //Льготный ли период
    @Column(name = "benefitsOn")
    private boolean benefitsOn;

   //Член правления в этом периоде
    @Column(name = "member_board_on")
    private boolean memberBoardOn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public int getContribute() {
        return contribute;
    }

    public void setContribute(int contribute) {
        this.contribute = contribute;
    }

    public int getContLand() {
        return contLand;
    }

    public void setContLand(int contLand) {
        this.contLand = contLand;
    }

    public int getContTarget() {
        return contTarget;
    }

    public void setContTarget(int contTarget) {
        this.contTarget = contTarget;
    }

    public int getFines() {
        return fines;
    }

    public void setFines(int fines) {
        this.fines = fines;
    }

    public boolean isFinesOn() {
        return finesOn;
    }

    public void setFinesOn(boolean finesOn) {
        this.finesOn = finesOn;
    }

    public Calendar getFinesLastUpdate() {
        return finesLastUpdate;
    }

    public void setFinesLastUpdate(Calendar finesLastUpdate) {
        this.finesLastUpdate = finesLastUpdate;
    }

    public boolean isBenefitsOn() {
        return benefitsOn;
    }

    public void setBenefitsOn(boolean benefitsOn) {
        this.benefitsOn = benefitsOn;
    }

    public boolean isMemberBoardOn() {
        return memberBoardOn;
    }

    public void setMemberBoardOn(boolean memberBoardOn) {
        this.memberBoardOn = memberBoardOn;
    }

    public int getSumFixed() {
        return this.contribute + this.contLand + this.contTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contribution that = (Contribution) o;
        return Objects.equals(id, that.id) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year);
    }
}
