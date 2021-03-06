package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.RentView;
import ru.kircoop.gk23.entity.Rent;

/**
 * Конвертер Garag
 */
@Service
public class RentConverter {

    public RentView map(Rent rent) {
        if (rent == null) return null;
        RentView dto = new RentView();
        dto.setId(rent.getId());
        dto.setYearRent(rent.getYearRent());
        dto.setContributeMax(rent.getContributeMax());
        dto.setContLandMax(rent.getContLandMax());
        dto.setContTargetMax(rent.getContTargetMax());
        return dto;
    }

    public Rent fromView(RentView dto){
        if (dto == null) return null;
        Rent rent = new Rent();
        rent.setId(dto.getId());
        rent.setYearRent(dto.getYearRent());
        rent.setContributeMax(dto.getContributeMax());
        rent.setContLandMax(dto.getContLandMax());
        rent.setContTargetMax(dto.getContTargetMax());
        return rent;
    }
}
