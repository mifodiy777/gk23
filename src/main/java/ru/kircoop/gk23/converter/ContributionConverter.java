package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.ContributionView;
import ru.kircoop.gk23.entity.Contribution;

@Service
public class ContributionConverter {

    public ContributionView map(Contribution c) {
        if (c == null) return null;
        ContributionView dto = new ContributionView();
        dto.setId(c.getId());
        dto.setYear(c.getYear());
        dto.setContribute(c.getContribute());
        dto.setContLand(c.getContLand());
        dto.setContTarget(c.getContTarget());
        dto.setFines(c.getFines());
        dto.setFinesOn(c.isFinesOn());
        dto.setFinesLastUpdate(c.getFinesLastUpdate());
        dto.setBenefitsOn(c.isBenefitsOn());
        dto.setMemberBoardOn(c.isMemberBoardOn());
        dto.setSum(c.getSumFixed() + c.getFines());
        return dto;
    }

    public Contribution fromView(ContributionView contribution) {
        if (contribution == null) return null;
        Contribution entityContribute = new Contribution();
        entityContribute.setId(contribution.getId());
        entityContribute.setYear(contribution.getYear());
        entityContribute.setContribute(contribution.getContribute());
        entityContribute.setContLand(contribution.getContLand());
        entityContribute.setContTarget(contribution.getContTarget());
        entityContribute.setFines(contribution.getFines());
        entityContribute.setFinesOn(contribution.isFinesOn());
        entityContribute.setFinesLastUpdate(contribution.getFinesLastUpdate());
        entityContribute.setBenefitsOn(contribution.isBenefitsOn());
        entityContribute.setMemberBoardOn(contribution.isMemberBoardOn());
        return entityContribute;
    }

}
