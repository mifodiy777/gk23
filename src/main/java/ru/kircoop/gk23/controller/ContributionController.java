package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kircoop.gk23.converter.ContributionConverter;
import ru.kircoop.gk23.converter.GaragConverter;
import ru.kircoop.gk23.converter.RentConverter;
import ru.kircoop.gk23.dto.ContributionView;
import ru.kircoop.gk23.entity.Contribution;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Rent;
import ru.kircoop.gk23.service.ContributionService;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.RentService;

import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private ContributionConverter converter;

    @Autowired
    private GaragConverter garagConverter;

    @Autowired
    private RentConverter rentConverter;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributionController.class);

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
                                     @RequestParam("year") Integer year, Model map, HttpServletResponse response) {
        try {
            Contribution contribution = contributionService.getContributionByGaragAndYear(id, year);
            Garag garag = garagService.getGarag(id);
            Rent rent = rentService.findByYear(year);

            map.addAttribute("contribution", converter.map(contribution));
            map.addAttribute("garag", garagConverter.map(garag));
            map.addAttribute("max", rentConverter.map(rent));

            return "modalEditContribute";
        } catch (DataAccessException e) {
            LOGGER.error("Невозможно получить форму долгового периода, ошибка БД!");
            map.addAttribute("message", "Невозможно получить форму долгового периода, ошибка БД!");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Добавление старых периодов
     *
     * @param contribute Период
     * @param id         ID Гаража
     * @param map        Model
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
                contributionService.saveOrUpdate(converter.fromView(contribute));
            } else {
                garag.getContributions().add(converter.fromView(contribute));
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
