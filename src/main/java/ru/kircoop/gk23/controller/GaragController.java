package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kircoop.gk23.converter.*;
import ru.kircoop.gk23.dto.*;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;
import ru.kircoop.gk23.entity.Person;
import ru.kircoop.gk23.exception.ServiceException;
import ru.kircoop.gk23.service.*;
import ru.kircoop.gk23.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private GaragConverter garagConverter;

    @Autowired
    private RentConverter rentConverter;

    @Autowired
    private PersonConverter personConverter;

    @Autowired
    private ContributionConverter contributionConverter;

    @Autowired
    private PaymentConverter paymentConverter;

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
    public ResponseEntity<String> getGarag(@RequestParam(required = false, value = "garag") Integer garag,
                                           @RequestParam("setSeries") String series) {
        if (garag != null) {
            return ResponseUtils.convertListToJson(Collections.singletonList(garagConverter.map(garagService.getGarag(garag))));
        }
        List<GaragView> garags = garagService.findBySeries(series).stream()
                .map(garagConverter::map).collect(Collectors.toCollection(ArrayList::new));
        return ResponseUtils.convertListToJson(garags);
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
        } catch (DataAccessException e) {
            model.addAttribute("textError", "Ошибка базы данных, проверте подключение к БД");
            return "errorPage";
        }
    }

    /**
     * Переход со страницы Владельцев к определенному гаражу
     *
     * @param id     ID гаража
     * @param series ряд гаража
     * @param model  Model
     * @return garags.jsp
     */
    @GetMapping(value = "linkGarag")
    public String linkGarag(@RequestParam("id") Integer id, @RequestParam("series") String series,
                            Model model) {
        model.addAttribute("setSeries", series);// ряд
        try {
            model.addAttribute("series", garagService.getSeries()); //список рядов для nav-tabs
            model.addAttribute("garagId", id); // id выбранного гаража
            return "garags";
        } catch (DataAccessException e) {
            model.addAttribute("textError", "Ошибка базы данных, проверте подключение к БД");
            return "errorPage";
        }
    }

    /**
     * Отображение формы добавления гаража
     *
     * @param map ModelMap
     * @return garag.jsp
     */
    @GetMapping(value = "garag")
    public String addGaragForm(Model map) {
        List<RentView> rents = rentService.getRents().stream()
                .map(rentConverter::map).collect(Collectors.toCollection(ArrayList::new));
        map.addAttribute("type", "Режим добавления гаража");
        map.addAttribute("isOldGarag", false); //создание гаража
        map.addAttribute("rents", rents); // список периодов системы
        map.addAttribute("garag", new GaragView());
        return "garag";
    }

    /**
     * Отображение формы редактирования гаража
     *
     * @param id  id гаража
     * @param map ModelMap
     * @return garag.jsp
     */
    @GetMapping(value = "garag/{id}")
    public String editGaragForm(@PathVariable("id") Integer id, Model map) {
        List<RentView> rents = rentService.getRents().stream()
                .map(rentConverter::map).collect(Collectors.toCollection(ArrayList::new));
        map.addAttribute("type", "Режим редактирования гаража");
        map.addAttribute("isOldGarag", true); //редактирование гаража
        map.addAttribute("rents", rents);
        map.addAttribute("garag", garagConverter.map(garagService.getGarag(id)));
        return "garag";
    }

    /**
     * Отображение формы замены владельца гаража
     *
     * @param id  id Гаража
     * @param map ModelMap
     * @return changePerson.jsp
     */
    @GetMapping(value = "changePerson/{id}")
    public String changePerson(@PathVariable("id") Integer id, Model map) {
        GaragView garag = garagConverter.map(garagService.getGarag(id));
        map.addAttribute("garag", garag);
        map.addAttribute("person", garag.getPerson()); //для Spring формы требуется отдельная подгрузка аттрибута //todo может убрать?
        return "changePerson";
    }

    /**
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
     */
    @PostMapping(value = "change")
    public String changePerson(Person person,
                               @RequestParam("garag") Integer garagId,
                               @RequestParam("searchPerson") Boolean searchPerson,
                               @RequestParam("deletePerson") Boolean deletePerson,
                               @RequestParam("oldPerson") Integer oldPersonId,
                               @RequestParam("countGarag") Boolean oneGarag,
                               @RequestParam("reason") String reason, Model map) {
        garagService.changePerson(garagService.getGarag(garagId), person, searchPerson, deletePerson, oldPersonId, oneGarag, reason);
        LOGGER.info("Владелец заменен!(" + person.getFIO() + ")");
        map.addAttribute("message", "Владелец заменен!");
        return "success";
    }


    //Информационно модальное окно для гаража

    /**
     * Информационное окно для гаража - платежи, долги.
     *
     * @param id  ID Гаража
     * @param map ModelMap
     * @return garagInf.jsp
     */
    @GetMapping(value = "garagInf")
    public String payModal(@RequestParam("idGarag") Integer id, Model map, HttpServletResponse response) {
        Garag garag = garagService.getGarag(id);
        if (garag.getContributions().isEmpty()) {
            map.addAttribute("message", "У гаража отсутствуют периоды начислений.\n Добавте их в меню редактирования гаража!");
            response.setStatus(409);
            return "error";
        }
        List<Contribution> contributionList = contributionService.getContributionByGarag(garag);
        List<ContributionView> contributionViews = contributionList.stream()
                .map(contributionConverter::map).collect(Collectors.toCollection(ArrayList::new));
        Contribution sumContribution = contributionService.getSumContribution(contributionList);
        List<Payment> payments = paymentService.getPaymentOnGarag(garag);
        List<PaymentView> paymentViews = payments.stream().limit(10).map(paymentConverter::map).collect(Collectors.toList());
        map.addAttribute("contributionsList", contributionViews);
        map.addAttribute("contributionSum", contributionConverter.map(sumContribution));
        map.addAttribute("total", sumContribution.getSumFixed() + sumContribution.getFines() + garag.getOldContribute());
        map.addAttribute("payments", paymentViews);
        map.addAttribute("garag", garag);
        map.addAttribute("fio", garag.getPerson().getFIO());
        return "garagInf";
    }

    /**
     * Печатная форма информации по гаражу - платежи, долги.
     *
     * @param id  ID Гаража
     * @param map ModelMap
     * @return infPrint.jsp
     */
    @GetMapping(value = "infPrint/{id}")
    public String infGarag(@PathVariable("id") Integer id, Model map) {
        Garag garag = garagService.getGarag(id);
        map.addAttribute("contributionAll", garagService.sumContribution(garag));
        map.addAttribute("garag", garag);
        map.addAttribute("fio", garag.getPerson().getFIO());
        map.addAttribute("now", Calendar.getInstance().getTime());
        return "infPrint";
    }

    /**
     * Сохранения гаража
     *
     * @param garag    Гараж
     * @param map      ModelMap
     * @param response ответ
     * @return сообщение о результате
     */
    @PostMapping(value = "saveGarag")
    public String saveGarag(Garag garag, Model map, HttpServletResponse response) {
        try {
            garagService.saveOrUpdate(garag);
            LOGGER.info("Гараж " + garag.getName() + " сохранен!");
            contributionService.updateFines();
            map.addAttribute("message", "Гараж сохранен!");
            return "success";
        } catch (ServiceException e) {
            LOGGER.error("Невозможно сохранить гараж, он уже существует");
            map.addAttribute("message", "Невозможно создать гараж, так как он уже существует!");
            response.setStatus(409);
            return "error";
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            map.addAttribute("message", "Ошибка по работе с БД!");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Поиск имеющихся владельцев
     *
     * @param pattern Часть ФИО для поиска
     * @param map     ModelMap
     * @return страница personRes.jsp со список подходящих владельцев по имеющейся части ФИО
     */
    @PostMapping(value = "searchPerson")
    public String searchPerson(@RequestParam("pattern") String pattern, Model map) {

        List<PersonView> persons = personService.findByfio(pattern).stream()
                .map(personConverter::map).collect(Collectors.toCollection(ArrayList::new));
        map.addAttribute("persons", persons);
        return "personRes";
    }

    /**
     * Определенный владелец в формате JSON
     *
     * @param id ID Владельца
     * @return json - ответ, найденный владелец.
     */
    @GetMapping(value = "getPerson")
    public @ResponseBody
    PersonView getPerson(@RequestParam("personId") Integer id) {
        return personConverter.map(personService.getPerson(id));
    }

    /**
     * Удаление гаража
     *
     * @param id       ID Гаража
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления гаража
     */
    @RequestMapping(value = "deleteGarag/{id}", method = RequestMethod.POST)
    public String deleteGarag(@PathVariable("id") Integer id, Model map, HttpServletResponse response) {
        try {
            Garag garag = garagService.getGarag(id);
            garagService.delete(id);
            LOGGER.info("Гараж " + garag.getName() + " удален!");
            map.addAttribute("message", "Гараж удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить, так как гараж используется!");
            response.setStatus(409);
            return "error";
        }
    }
}
