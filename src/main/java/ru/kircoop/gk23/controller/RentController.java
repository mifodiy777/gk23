package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kircoop.gk23.converter.RentConverter;
import ru.kircoop.gk23.dto.RentView;
import ru.kircoop.gk23.entity.Rent;
import ru.kircoop.gk23.service.RentService;

import javax.servlet.http.HttpServletResponse;

@Controller
public class RentController {

    @Autowired
    private RentService rentService;

    @Autowired
    private RentConverter converter;

    private static final Logger LOGGER = LoggerFactory.getLogger(RentController.class);

    /**
     * Проверка на существование определенного начисления
     *
     * @param year     год начисления
     * @param map      ModelMap
     * @param response ответ
     * @return Если не существует начисление текущего года, то отображается форма создания нового начисления modalNewRent.jsp
     * Если существует, то отображается ошибка создания нового начисления.
     */
    @GetMapping(value = "checkYearRent")
    public String checkYearRent(@RequestParam("year") Integer year, Model map,
                                HttpServletResponse response) {
        if (rentService.checkRent(year)) {
            map.addAttribute("year", year);
            map.addAttribute("rent", new RentView());
            return "modalNewRent";
        } else {
            map.addAttribute("message", "Период текущего года существует");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Сохранения нового начисления
     *
     * @param rent Начисление
     * @param map  ModelMap
     * @return Сообщение о результате сохранения нового начисления
     */
    @PostMapping(value = "saveRent")
    public String saveRent(RentView rent, Model map) {
        Rent entityRent = converter.fromView(rent);
        rentService.saveOrUpdate(entityRent);
        rentService.createNewPeriod(entityRent);
        LOGGER.info("Создан новый период-" + rent.getYearRent());
        map.addAttribute("message", "Сумма оплаты за " + rent.getYearRent() + " год введена!");
        return "success";
    }
}
