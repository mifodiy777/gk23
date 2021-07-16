package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kircoop.gk23.converter.GaragConverter;
import ru.kircoop.gk23.dto.GaragView;
import ru.kircoop.gk23.service.ContributionService;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.PersonService;
import ru.kircoop.gk23.service.RentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контороллер по работе с гаражами
 * Created by Кирилл on 25.07.2015.
 */
@Controller
public class GaragController {

    @Autowired
    private GaragService garagService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private RentService rentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GaragController.class);

    /*
     * Получение списка гаражей
     *
     * @param garag  ID Гаража, по умолчанию этого переходп нет.
     *               Параметр появляется только после перехода в гаражи из списка владельцев
     * @param series Ряд
     * @return JSON список гаражей
     */
    @GetMapping(value = "/allGarag")
    public ResponseEntity<List<GaragView>> getGarag(@RequestParam("setSeries") String series) {
        List<GaragView> garags = garagService.findBySeries(series).stream()
                .map(GaragConverter::map).collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(garags, HttpStatus.OK);
    }

    /*
     * Получение страницы всех гаражей
     *
     * @param series ряд, по умолчанию 1.
     * @param map    Model map
     * @return garags.jsp
     */
    @GetMapping(value = "garagPage")
    public String getGaragsPage(@RequestParam(defaultValue = "1", value = "series") String series, Model model) {
        model.addAttribute("setSeries", series); //ряд, по умолчанию выбирается ряд "1"
        try {
            model.addAttribute("series", garagService.getSeries()); //список рядов для nav-tabs
            return "garags";
        } catch (DataAccessResourceFailureException e) {
            model.addAttribute("textError", "Ошибка базы данных, проверте подключение к БД");
            return "errorPage";
        }
    }

    /**
     * Получение списка гаражей
     *
     * @param garag  ID Гаража, по умолчанию этого переходп нет.
     *               Параметр появляется только после перехода в гаражи из списка владельцев
     * @param series Ряд
     * @return JSON список гаражей
     */
/*    @RequestMapping(value = "allGarag", method = RequestMethod.GET)
    public ResponseEntity<String> getGarag(@RequestParam(required = false, value = "garag") Integer garag,
                                           @RequestParam("setSeries") String series) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Person.class, new PersonAdapter());
        if (garag != null) {
            return Utils.convertObjectToJson(gsonBuilder, garagService.getGarag(garag));
        }
        return Utils.convertListToJson(gsonBuilder, garagService.findBySeries(series));
    }*/

    /**
     * Переход со страницы Владельцев к определенному гаражу
     *
     * @param id     ID гаража
     * @param series ряд гаража
     * @param map    ModelMap
     * @return garags.jsp
     *//*
    @RequestMapping(value = "linkGarag", method = RequestMethod.GET)
    public String linkGarag(@RequestParam("id") Integer id, @RequestParam("series") String series,
                            ModelMap map) {
        map.addAttribute("setSeries", series);// ряд
        try {
            map.addAttribute("series", garagService.getSeries()); //список рядов для nav-tabs
            map.addAttribute("garagId", id); // id выбранного гаража
            return "garags";
        } catch (DataAccessResourceFailureException e) {
            map.addAttribute("textError", "Ошибка базы данных, проверте подключение к БД");
            return "errorPage";
        }
    }

    /**
     * Отображение формы добавления гаража
     *
     * @param map ModelMap
     * @return garag.jsp
     *//*
    @RequestMapping(value = "garag", method = RequestMethod.GET)
    public String addGaragForm(ModelMap map, HttpServletResponse response) {
        map.addAttribute("type", "Режим добавления гаража");
        map.addAttribute("isOldGarag", false); //создание гаража
        map.addAttribute("rents", rentService.getRents()); // список периодов системы
        map.addAttribute("garag", new Garag());
        return "garag";
    }

    *//**
     * Отображение формы редактирования гаража
     *
     * @param id  id гаража
     * @param map ModelMap
     * @return garag.jsp
     *//*
    @RequestMapping(value = "garag/{id}", method = RequestMethod.GET)
    public String editGaragForm(@PathVariable("id") Integer id, ModelMap map) {
        map.addAttribute("type", "Режим редактирования гаража");
        map.addAttribute("isOldGarag", true); //редактирование гаража
        map.addAttribute("rents", rentService.findAll());
        map.addAttribute("garag", garagService.getGarag(id));
        return "garag";
    }

    *//**
     * Отображение формы замены владельца гаража
     *
     * @param id  id Гаража
     * @param map ModelMap
     * @return changePerson.jsp
     *//*
    @RequestMapping(value = "changePerson/{id}", method = RequestMethod.GET)
    public String changePerson(@PathVariable("id") Integer id, ModelMap map) {
        Garag garag = garagService.getGarag(id);
        map.addAttribute("garag", garag);
        map.addAttribute("person", garag.getPerson()); //для Spring формы требуется отдельная подгрузка аттрибута
        return "changePerson";
    }

    *//**
     * Метод для замены владельца у гаража/гаражей
     *
     * @param garagId      id гаража
     * @param oldPersonId  id прошлого владельца
     * @param person       Владелец
     * @param searchPerson Выполнялся ли поиск и замена владельца
     * @param deletePerson Удалять ли предыдущего владельца
     * @param oneGarag     Замена только ли у текущего гаража
     * @param reason       Описание  причины смены владельца
     * @param map          ModelMap
     * @return сообщение об успешном выполнении замены владельца
     *//*
    @RequestMapping(value = "change", method = RequestMethod.POST)
    public String changePerson(Person person,
                               @RequestParam("garag") Integer garagId,
                               @RequestParam("searchPerson") Boolean searchPerson,
                               @RequestParam("deletePerson") Boolean deletePerson,
                               @RequestParam("oldPerson") Integer oldPersonId,
                               @RequestParam("countGarag") Boolean oneGarag,
                               @RequestParam("reason") String reason, ModelMap map) {
        garagService.changePerson(garagService.getGarag(garagId),person,searchPerson,deletePerson,oldPersonId,oneGarag,reason);
        logger.info("Владелец заменен!(" + person.getFIO() + ")");
        map.put("message", "Владелец заменен!");
        return "success";
    }


    //Информационно модальное окно для гаража

    *//**
     * Информационное окно для гаража - платежи, долги.
     *
     * @param id  ID Гаража
     * @param map ModelMap
     * @return garagInf.jsp
     *//*
    @RequestMapping(value = "garagInf", method = RequestMethod.GET)
    public String payModal(@RequestParam("idGarag") Integer id, ModelMap map, HttpServletResponse response) {
        Garag garag = garagService.getGarag(id);
        if(garag.getContributions().isEmpty()){
            map.put("message", "У гаража отсутствуют периоды начислений.\n Добавте их в меню редактирования гаража!");
            response.setStatus(409);
            return "error";
        }
        map.addAttribute("contributionAll", garagService.sumContribution(garag));
        map.addAttribute("garag", garag);
        map.addAttribute("fio", garag.getPerson().getFIO());
        return "garagInf";
    }

    *//**
     * Печатная форма информации по гаражу - платежи, долги.
     *
     * @param id  ID Гаража
     * @param map ModelMap
     * @return infPrint.jsp
     *//*
    @RequestMapping(value = "infPrint/{id}", method = RequestMethod.GET)
    public String infGarag(@PathVariable("id") Integer id, ModelMap map) {
        Garag garag = garagService.getGarag(id);
        map.addAttribute("contributionAll", garagService.sumContribution(garag));
        map.addAttribute("garag", garag);
        map.addAttribute("fio", garag.getPerson().getFIO());
        map.addAttribute("now", Calendar.getInstance().getTime());
        return "infPrint";
    }

    *//**
     * Сохранения гаража
     * @param garag Гараж
     * @param map ModelMap
     * @param response ответ
     * @return сообщение о результате
     *//*
    @RequestMapping(value = "saveGarag", method = RequestMethod.POST)
    public String saveGarag(Garag garag, ModelMap map, HttpServletResponse response) {
        try {
            garagService.saveOrUpdate(garag);
            logger.info("Гараж " + garag.getName() + " сохранен!");
            contributionService.updateFines();
            map.put("message", "Гараж сохранен!");
            return "success";
        } catch (ExistGaragException e) {
            logger.error("Невозможно сохранить гараж, он уже существует");
            map.put("message", "Невозможно создать гараж, так как он уже существует!");
            response.setStatus(409);
            return "error";
        } catch (DataIntegrityViolationException e) {
            logger.error(e.getMessage());
            map.put("message", "Ошибка по работе с БД!");
            response.setStatus(409);
            return "error";
        }
    }

    *//**
     * Поиск имеющихся владельцев
     *
     * @param pattern Часть ФИО для поиска
     * @param map ModelMap
     * @return страница personRes.jsp со список подходящих владельцев по имеющейся части ФИО
     *//*
    @RequestMapping(value = "searchPerson", method = RequestMethod.POST)
    public String searchPerson(@RequestParam("pattern") String pattern, ModelMap map) {
        List<Person> persons = personService.findByfio(pattern);
        map.addAttribute("persons", persons);
        return "personRes";
    }

    *//**
     * Определенный владелец в формате JSON
     * @param id ID Владельца
     * @return json - ответ, найденный владелец.
     *//*
    @RequestMapping(value = "getPerson", method = RequestMethod.GET)
    public ResponseEntity<String> getPerson(@RequestParam("personId") Integer id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        return Utils.createJsonResponse(gsonBuilder, personService.getPerson(id));
    }

    *//**
     * Удаление гаража
     * @param id ID Гаража
     * @param map ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления гаража
     *//*
    @RequestMapping(value = "deleteGarag/{id}", method = RequestMethod.POST)
    public String deleteGarag(@PathVariable("id") Integer id, ModelMap map, HttpServletResponse response) {
        try {
            Garag garag = garagService.getGarag(id);
            garagService.delete(id);
            logger.info("Гараж " + garag.getName() + " удален!");
            map.put("message", "Гараж удален!");
            return "success";
        } catch (DataIntegrityViolationException e) {
            map.put("message", "Невозможно удалить, так как гараж используется!");
            response.setStatus(409);
            return "error";
        }
    }*/
}
