package ru.kircoop.gk23.dto;

import java.io.Serializable;

public class Garag implements Serializable {

    private String series;

    private String number;

    public Garag() {
    }

    public Garag(String series, String number) {
        this.series = series;
        this.number = number;
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
}
