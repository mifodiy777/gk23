package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kircoop.gk23.converter.ContributionConverter;
import ru.kircoop.gk23.dto.ContributionView;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.service.ContributionService;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.RentService;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

/**
 * Контроллер по работе с долговыми периодами
 */
@Controller
public class ContributionController {

    @Autowired
    private GaragService garagService;

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private RentService rentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionController.class);

/*    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Calendar.class, new CalendarCustomEditor());
    }*/

    /**
     * Форма добавления старых периодов
     *
     * @param id       ID Гаража
     * @param year     год периода
     * @param map      ModelMap
     * @param response Response
     * @return страница modalEditContribute.jsp
     */
    @RequestMapping(value = "editContribute", method = RequestMethod.GET)
    public String editContributeForm(@RequestParam("idGarag") Integer id,
                                     @RequestParam("year") Integer year, ModelMap map, HttpServletResponse response) {
        try {
            map.addAttribute("contribution", contributionService.getContributionByGaragAndYear(id, year));
            map.addAttribute("garag", garagService.getGarag(id));
            map.addAttribute("max", rentService.findByYear(year));
            return "modalEditContribute";
        } catch (DataAccessException e) {
            LOGGER.error("Невозможно получить форму долгового периода, ошибка БД!");
            map.put("message", "Невозможно получить форму долгового периода, ошибка БД!");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Добавление старых периодов
     *
     * @param contribute Период
     * @param id         ID Гаража
     * @param map        ModelMap
     * @param response   Response
     * @return
     */
    @RequestMapping(value = "saveContribute", method = RequestMethod.POST)
    public String saveContribute(ContributionView contribute,
                                 @RequestParam("idGarag") Integer id,
                                 Model map, HttpServletResponse response) {
        Garag garag = garagService.getGarag(id);
        try {
            if (contribute.getId() != null) {
                contributionService.saveOrUpdate(ContributionConverter.fromView(contribute));
            } else {
                garag.getContributions().add(ContributionConverter.fromView(contribute));
                garagService.save(garag);
            }
            LOGGER.info("Долг для гаража " + garag.getName() + " за " +
                    contribute.getYear() + " год назначен");
            map.addAttribute("message", "Долг за " + contribute.getYear() + " год введен успешно!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно сохранить долг");
            response.setStatus(409);
            return "error";
        }
    }
}
