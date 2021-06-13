package ru.kircoop.gk23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class HistoryGarag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_history", nullable = false)
    private Integer id;

    @Column(name = "date_record")
    private LocalDateTime dateRecord;

    @Column(name = "fio_last_person")
    private String fioLastPerson;

    @Column(name = "reason")
    private String reason;

    public HistoryGarag() {
    }

    public HistoryGarag(LocalDateTime dateRecord, String fioLastPerson, String reason, Garag garag) {
        this.dateRecord = dateRecord;
        this.fioLastPerson = fioLastPerson;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateRecord() {
        return dateRecord;
    }

    public void setDateRecord(LocalDateTime dateRecord) {
        this.dateRecord = dateRecord;
    }

    public String getFioLastPerson() {
        return fioLastPerson;
    }

    public void setFioLastPerson(String fioLastPerson) {
        this.fioLastPerson = fioLastPerson;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryGarag that = (HistoryGarag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
