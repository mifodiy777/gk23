package ru.kircoop.gk23.dto;

import lombok.Data;

@Data
public class HistoryView {

    private Integer id;
    private String dateRecord;
    private String fioLastPerson;
    private String reason;
}
