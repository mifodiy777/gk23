package ru.kircoop.gk23.converter;

import ru.kircoop.gk23.dto.GaragView;
import ru.kircoop.gk23.entity.Garag;

/**
 * Конвертер Garag
 */
public class GaragConverter {

    public static GaragView map(Garag garag) {
        if (garag == null) return null;
        GaragView dto = new GaragView();
        dto.setId(garag.getId());
        dto.setSeries(garag.getSeries());
        dto.setNumber(garag.getNumber());
        dto.setPerson(PersonConverter.map(garag.getPerson()));
        dto.setOldContribute(garag.getOldContribute());
        dto.setAdditionalInformation(garag.getAdditionalInformation());
        return dto;
    }
}
