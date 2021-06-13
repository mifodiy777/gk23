package ru.kircoop.gk23.converter;

import ru.kircoop.gk23.dto.PersonView;
import ru.kircoop.gk23.entity.Person;

/**
 * Конвертер Person
 */
public class PersonConverter {

    public static PersonView map(Person person) {
        if (person == null) return null;
        PersonView dto = new PersonView();
        dto.setId(person.getId());
        dto.setLastName(person.getLastName());
        dto.setName(person.getName());
        dto.setFatherName(person.getFatherName());
        dto.setCity(person.getCity());
        dto.setStreet(person.getStreet());
        dto.setHome(person.getHome());
        dto.setApartment(person.getApartment());
        dto.setTelephone(person.getTelephone());
        dto.setBenefits(person.getBenefits());
        dto.setMemberBoard(person.getMemberBoard());
        return dto;
    }
}
