package ru.kircoop.gk23.converter;

import org.springframework.stereotype.Service;
import ru.kircoop.gk23.dto.PersonView;
import ru.kircoop.gk23.entity.Person;

/**
 * Конвертер Person
 */
@Service
public class PersonConverter {

    public PersonView map(Person person) {
        if (person == null) return null;
        PersonView dto = new PersonView();
        dto.setId(person.getId());
        dto.setLastName(person.getLastName());
        dto.setName(person.getName());
        dto.setFatherName(person.getFatherName());
        dto.setFio(person.getLastName() + " " + person.getName() + " " + person.getFatherName());
        dto.setCity(person.getCity());
        dto.setStreet(person.getStreet());
        dto.setHome(person.getHome());
        dto.setApartment(person.getApartment());
        dto.setFullAddress(getAddr(person));
        dto.setTelephone(person.getTelephone());
        dto.setBenefits(person.getBenefits());
        dto.setMemberBoard(person.getMemberBoard());
        return dto;
    }

    public String getAddr(Person p) {
        StringBuilder addr = new StringBuilder();
        addr.append("г.");
        addr.append(p.getCity());
        addr.append(" ул.");
        addr.append(p.getStreet());
        addr.append(" д.");
        addr.append(p.getHome());
        if (!p.getApartment().isEmpty()) {
            addr.append(" кв.");
            addr.append(p.getApartment());
        }
        return addr.toString();
    }

    public Person fromView(PersonView view) {
        if (view == null) return null;
        Person pojo = new Person();
        pojo.setId(view.getId());
        pojo.setLastName(view.getLastName());
        pojo.setName(view.getName());
        pojo.setFatherName(view.getFatherName());
        pojo.setCity(view.getCity());
        pojo.setStreet(view.getStreet());
        pojo.setApartment(view.getApartment());
        pojo.setBenefits(view.getBenefits());
        pojo.setTelephone(view.getTelephone());
        pojo.setMemberBoard(view.getMemberBoard());
        return pojo;
    }
}
