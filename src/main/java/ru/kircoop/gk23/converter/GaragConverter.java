package ru.kircoop.gk23.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.GaragView;
import ru.kircoop.gk23.entity.Garag;

/**
 * Конвертер Garag
 */
@Service
public class GaragConverter {

    @Autowired
    private PersonConverter converter;

    public GaragView map(Garag garag) {
        if (garag == null) return null;
        GaragView dto = new GaragView();
        dto.setId(garag.getId());
        dto.setSeries(garag.getSeries());
        dto.setNumber(garag.getNumber());
        dto.setFullName(  garag.getFullName());
        dto.setPerson(converter.map(garag.getPerson()));
        dto.setOldContribute(garag.getOldContribute());
        dto.setAdditionalInformation(garag.getAdditionalInformation());
        return dto;
    }
}
