package ru.kircoop.gk23.converter;

import ru.kircoop.gk23.dto.CostTypeView;
import ru.kircoop.gk23.entity.CostType;

/**
 * Конвертер Garag
 */
public class CostTypeConverter {

    public static CostTypeView map(CostType type) {
        if (type == null) return null;
        CostTypeView dto = new CostTypeView();
        dto.setId(type.getId());
        dto.setName(type.getName());
        return dto;
    }

    public static CostType fromView(CostTypeView dto) {
        if (dto == null) return null;
        CostType type = new CostType();
        type.setId(dto.getId());
        type.setName(dto.getName());
        return type;
    }

}
