package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.ContributionView;
import ru.kircoop.gk23.dto.CostView;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Cost;
import ru.kircoop.gk23.entity.CostType;

import java.time.LocalDate;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_DOT;

/**
 * Конвертер Garag
 */
@Service
public class CostConverter {

    public CostView map(Cost cost) {
        if (cost == null) return null;
        CostView dto = new CostView();
        dto.setId(cost.getId());
        dto.setTypeName(cost.getType().getName());
        dto.setDescription(cost.getDescription());
        dto.setMoney(cost.getMoney());
        dto.setDate(cost.getDate().format(DD_MM_YYYY_DOT));
        return dto;
    }

    public Cost fromView(CostView dto, CostType type) {
        if (dto == null) return null;
        Cost cost = new Cost();
        cost.setId(dto.getId());
        cost.setType(type);
        cost.setDescription(dto.getDescription());
        cost.setMoney(dto.getMoney());
        cost.setDate(LocalDate.parse(dto.getDate(), DD_MM_YYYY_DOT));
        return cost;
    }
}
