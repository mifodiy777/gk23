package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.HistoryView;
import ru.kircoop.gk23.entity.HistoryGarag;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_WITH_TIME;

@Service
public class HistoryConverter {

    public HistoryView map(HistoryGarag history) {
        if (history == null) return null;
        HistoryView dto = new HistoryView();
        dto.setId(history.getId());
        dto.setFioLastPerson(history.getFioLastPerson());
        dto.setReason(history.getReason());
        dto.setDateRecord(history.getDateRecord().format(DD_MM_YYYY_WITH_TIME));
        return dto;
    }

}
