package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kircoop.gk23.converter.GaragConverter;
import ru.kircoop.gk23.converter.PersonConverter;
import ru.kircoop.gk23.dto.PersonView;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Person;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.PersonService;
import ru.kircoop.gk23.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер персоны
 */
@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private GaragService garagService;

    @Autowired
    private PersonConverter converter;

    @Autowired
    private GaragConverter garagConverter;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    /**
     * Страница владельцев
     *
     * @return persons.html
     */
    @GetMapping(value = "persons")
    public String getPersonsPage() {
        return "persons";

    }

    /**
     * Получение владельцев любые 30 или по части ФИО
     *
     * @param fio Часть ФИО
     * @return JSON список владельцев
     */
    @GetMapping(value = "allPerson")
    public ResponseEntity<String> getPersons(@RequestParam("fio") String fio) {
        List<PersonView> persons = personService.getPersons(fio).stream()
                .map(converter::map)
                .collect(Collectors.toList());
        return ResponseUtils.convertListToJson(persons);
    }

    /**
     * Получение владельцев без гаража
     *
     * @return JSON список владельцев
     */
    @GetMapping(value = "emptyPersons")
    public ResponseEntity<String> getEmptyPersons() {
        List<PersonView> persons = personService.getEmptyPersons().stream()
                .map(converter::map)
                .collect(Collectors.toList());
        return ResponseUtils.convertListToJson(persons);
    }

    /**
     * Страница членов правления
     *
     * @return members.jsp
     */
    @GetMapping(value = "membersPage")
    public String getMembersPage() {
        return "members";
    }

    /**
     * Получение списка всех членов правления
     *
     * @return JSON список членов правления
     */
    @GetMapping(value = "members")
    public ResponseEntity<String> getMembers() {
        List<PersonView> persons = personService.getMembers().stream()
                .map(converter::map)
                .collect(Collectors.toList());
        return ResponseUtils.convertListToJson(persons);
    }

    /**
     * Форма добавления владельцев
     *
     * @param map ModelMap
     * @return person.jsp
     */
    @GetMapping(value = "person")
    public String addPersonForm(Model map) {
        map.addAttribute("type", "Режим добавления владельца");
        map.addAttribute("person", new PersonView());
        return "person";
    }

    /**
     * Сохранение владельца
     *
     * @param personView Владелец
     * @param map        ModelMap
     * @return Сообщение о результате сохранения владельца
     */
    @PostMapping(value = "savePerson")
    public String savePerson(PersonView personView, Model map) {
        Person person = converter.fromView(personView);
        personService.saveOrUpdate(person);
        LOGGER.info("Владелец сохранен!(" + person.getFIO() + ")");
        map.addAttribute("message", "Владелец сохранен!");
        return "success";
    }

    /**
     * Форма редактирования владельца
     *
     * @param id  ID владельца
     * @param map ModelMap
     * @return person.jsp
     */
    @GetMapping(value = "person/{id}")
    public String editPersonForm(@PathVariable("id") Integer id, Model map) {
        map.addAttribute("type", "Режим редактирования владельца");
        map.addAttribute("person", converter.map(personService.getPerson(id)));
        List<Garag> garags = garagService.findByPersonId(id);
        if (garags != null) {
            map.addAttribute("garags", garags.stream().map(garagConverter::map).collect(Collectors.toList()));
        }
        return "person";
    }


    /**
     * Удаление назначения к гаражу из режима редактирования владельца
     *
     * @param idGarag  ID Гаража
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления назначения к гаражу
     */
    @PostMapping(value = "deleteGaragInPerson")
    public String deletePerson(@RequestParam("idGarag") Integer idGarag,
                               Model map, HttpServletResponse response) {
        try {
            Garag garag = garagService.getGarag(idGarag);
            garag.setPerson(null);
            garagService.save(garag);
            LOGGER.info("Удален гараж у владельца(" + garag.getName() + ")");
            map.addAttribute("message", "Назначение удаленно!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить, так как гараж используется!");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Удаление владельца
     *
     * @param id       ID владельца
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления владельца
     */
    @PostMapping(value = "deletePerson/{id}")
    public String deletePersonFromGarag(@PathVariable("id") Integer id, Model map, HttpServletResponse response) {
        try {
            for (Garag garag : garagService.findByPersonId(id)) {
                garag.setPerson(null);
                garagService.save(garag);
            }
            personService.delete(id);
            map.addAttribute("message", "Владелец удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить, так как владелец используется!");
            response.setStatus(409);
            return "error";
        }
    }
}
