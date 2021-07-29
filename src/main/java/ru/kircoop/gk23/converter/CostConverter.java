package ru.kircoop.gk23.converter;

import ru.kircoop.gk23.dto.CostView;
import ru.kircoop.gk23.entity.Cost;
import ru.kircoop.gk23.entity.CostType;

import java.time.LocalDate;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_DOT;

/**
 * Конвертер Garag
 */
public class CostConverter {

    public static CostView map(Cost cost) {
        if (cost == null) return null;
        CostView dto = new CostView();
        dto.setId(cost.getId());
        dto.setTypeName(cost.getType().getName());
        dto.setDescription(cost.getDescription());
        dto.setMoney(cost.getMoney());
        dto.setDate(cost.getDate().format(DD_MM_YYYY_DOT));
        return dto;
    }

    public static Cost fromView(CostView dto) {
        if (dto == null) return null;

        CostType type = new CostType();
        type.setId(dto.getTypeId());
        type.setName(dto.getTypeName());

        Cost cost = new Cost();
        cost.setId(dto.getId());
        cost.setType(type);
        cost.setDescription(dto.getDescription());
        cost.setMoney(dto.getMoney());
        cost.setDate(LocalDate.parse(dto.getDate(), DD_MM_YYYY_DOT));
        return cost;
    }
}
