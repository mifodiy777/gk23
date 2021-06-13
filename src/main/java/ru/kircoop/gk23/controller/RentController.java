package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kircoop.gk23.entity.Rent;
import ru.kircoop.gk23.service.RentService;

import javax.servlet.http.HttpServletResponse;

@RestController
public class RentController {

    @Autowired
    private RentService rentService;

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
    @RequestMapping(value = "checkYearRent", method = RequestMethod.GET)
    public String checkYearRent(@RequestParam("year") Integer year, ModelMap map,
                                HttpServletResponse response) {
        if (rentService.checkRent(year)) {
            map.addAttribute("year", year);
            map.addAttribute("rent", new Rent());
            return "modalNewRent";
        } else {
            map.put("message", "Период текущего года существует");
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
    @RequestMapping(value = "saveRent", method = RequestMethod.POST)
    public String saveRent(Rent rent, ModelMap map) {
        rentService.saveOrUpdate(rent);
        rentService.createNewPeriod(rent);
        LOGGER.info("Создан новый период-" + rent.getYearRent());
        map.put("message", "Сумма оплаты за " + rent.getYearRent() + " год введена!");
        return "success";
    }

}
