package ru.kircoop.gk23.converter;

import ru.kircoop.gk23.dto.ContributionView;
import ru.kircoop.gk23.entity.Contribution;

public class ContributionConverter {

    public static ContributionView map(Contribution contribution) {
        if (contribution == null) return null;
        ContributionView dto = new ContributionView();
        dto.setId(contribution.getId());
        dto.setYear(contribution.getYear());
        dto.setContribute(contribution.getContribute());
        dto.setContLand(contribution.getContLand());
        dto.setContTarget(contribution.getContTarget());
        dto.setFines(contribution.getFines());
        dto.setFinesOn(contribution.isFinesOn());
        dto.setFinesLastUpdate(contribution.getFinesLastUpdate());
        dto.setBenefitsOn(contribution.isBenefitsOn());
        dto.setMemberBoardOn(contribution.isMemberBoardOn());
        return dto;
    }

    public static Contribution fromView(ContributionView contribution) {
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
