package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.PersonService;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private GaragService garagService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    /* *//**
     * Страница владельцев
     *
     * @return persons.jsp
     *//*
    @RequestMapping(value = "persons", method = RequestMethod.GET)
    public ModelAndView getPersonsPage() {
        return new ModelAndView("persons");

    }

    *//**
     * Получение владельцев любые 30 или по части ФИО
     *
     * @param fio Часть ФИО
     * @return JSON список владельцев
     *//*
    @RequestMapping(value = "allPerson", method = RequestMethod.GET)
    public ResponseEntity<String> getPersons(@RequestParam("fio") String fio) {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Person.class, new PersonPageAdapter());
        return Utils.convertListToJson(gson, personService.getPersons(fio));
    }

        /**
     * Получение владельцев без гаража
     *
     * @return JSON список владельцев
     */
/*    @RequestMapping(value = "emptyPersons", method = RequestMethod.GET)
    public ResponseEntity<String> getEmptyPersons() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Person.class, new PersonPageAdapter());
        return Utils.convertListToJson(gson, personService.getEmptyPersons());
    }*/

    /**
     * Страница членов правления
     *
     * @return members.jsp
     *//*
    @RequestMapping(value = "membersPage", method = RequestMethod.GET)
    public ModelAndView getMembersPage() {
        return new ModelAndView("members");
    }

    *//**
     * Получение списка всех членов правления
     *
     * @return JSON список членов правления
     *//*
    @RequestMapping(value = "members", method = RequestMethod.GET)
    public ResponseEntity<String> getMembers() {
        GsonBuilder gson = new GsonBuilder();
        gson.excludeFieldsWithoutExposeAnnotation();
        gson.registerTypeAdapter(Person.class, new PersonAdapter());
        return Utils.convertListToJson(gson, personService.getMembers());
    }

    *//**
     * Форма добавления владельцев
     *
     * @param map ModelMap
     * @return person.jsp
     *//*
    @RequestMapping(value = "person", method = RequestMethod.GET)
    public String addPersonForm(ModelMap map) {
        map.addAttribute("type", "Режим добавления владельца");
        map.addAttribute("person", new Person());
        return "person";
    }

    *//**
     * Сохранение владельца
     *
     * @param person Владелец
     * @param map    ModelMap
     * @return Сообщение о результате сохранения владельца
     *//*
    @RequestMapping(value = "savePerson", method = RequestMethod.POST)
    public String savePerson(Person person, ModelMap map) {
        personService.saveOrUpdate(person);
        logger.info("Владелец сохранен!(" + person.getFIO() + ")");
        map.put("message", "Владелец сохранен!");
        return "success";
    }

    *//**
     * Форма редактирования владельца
     *
     * @param id  ID владельца
     * @param map ModelMap
     * @return person.jsp
     *//*
    @RequestMapping(value = "person/{id}", method = RequestMethod.GET)
    public String editPersonForm(@PathVariable("id") Integer id, ModelMap map) {
        map.addAttribute("type", "Режим редактирования владельца");
        map.addAttribute("person", personService.getPerson(id));
        return "person";
    }


    *//**
     * Удаление назначения к гаражу из режима редактирования владельца
     *
     * @param idGarag  ID Гаража
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления назначения к гаражу
     *//*
    @RequestMapping(value = "deleteGaragInPerson", method = RequestMethod.POST)
    public String deletePerson(@RequestParam("idGarag") Integer idGarag,
                               ModelMap map, HttpServletResponse response) {
        try {
            Garag garag = garagService.getGarag(idGarag);
            garag.setPerson(null);
            garagService.save(garag);
            logger.info("Удален гараж у владельца(" + garag.getName() + ")");
            map.put("message", "Назначение удаленно!");
            return "success";
        } catch (DataIntegrityViolationException e) {
            map.put("message", "Невозможно удалить, так как гараж используется!");
            response.setStatus(409);
            return "error";
        }
    }

    *//**
     * Удаление владельца
     *
     * @param id       ID владельца
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления владельца
     *//*
    @RequestMapping(value = "deletePerson/{id}", method = RequestMethod.POST)
    public String deleteGarag(@PathVariable("id") Integer id, ModelMap map, HttpServletResponse response) {
        try {
            for (Garag garag : personService.getPerson(id).getGaragList()) {
                garag.setPerson(null);
                garagService.save(garag);
            }
            Person person = personService.getPerson(id);
            logger.info("Владелец удаленн(" + person.getFIO() + ")");
            personService.delete(id);
            map.put("message", "Владелец удален!");
            return "success";
        } catch (DataIntegrityViolationException e) {
            map.put("message", "Невозможно удалить, так как владелец используется!");
            response.setStatus(409);
            return "error";
        }
    }*/
}
