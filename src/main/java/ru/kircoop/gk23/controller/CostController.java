package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kircoop.gk23.converter.CostConverter;
import ru.kircoop.gk23.converter.CostTypeConverter;
import ru.kircoop.gk23.dto.CostTypeView;
import ru.kircoop.gk23.dto.CostView;
import ru.kircoop.gk23.entity.Cost;
import ru.kircoop.gk23.entity.CostType;
import ru.kircoop.gk23.service.CostService;
import ru.kircoop.gk23.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер по работе с расходами
 */
@Controller
public class CostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CostController.class);

    @Autowired
    private CostService service;

    /**
     * Страница с формой добавления расходов
     *
     * @return страница cost.html
     */
    @GetMapping(value = "cost")
    public String addGaragForm(Model map) {
        map.addAttribute("cost", new CostView());
        return "cost";
    }

    /**
     * Отображение формы редактирования типа расхода
     *
     * @param id  id типа расхода
     * @param map Model
     * @return costType.html
     */
    @GetMapping(value = "costType/{id}")
    public String editGaragForm(@PathVariable("id") Integer id, Model map) {
        map.addAttribute("costType", CostTypeConverter.map(service.getType(id)));
        return "costType";
    }

    /**
     * Страница расходами
     *
     * @return страница costs.html
     */
    @GetMapping(value = "costsPage")
    public String getCostsPage() {
        return "costs";
    }

    /**
     * Страница с типами расходов
     *
     * @return страница costTypes.html
     */
    @GetMapping(value = "costTypesPage")
    public String getCostTypesPage() {
        return "costTypes";
    }

    /**
     * @return список всех расходов
     */
    @GetMapping(value = "getCosts")
    public ResponseEntity<String> getCosts() {
        List<CostView> viewList = service.getAll().stream().map(CostConverter::map).collect(Collectors.toList());
        return ResponseUtils.convertListToJson(viewList);
    }

    /**
     * @return список всех типов расходов
     */
    @GetMapping(value = "getCostTypes")
    public ResponseEntity<String> getCostType() {
        List<CostTypeView> typeViewList = service.getTypes().stream().map(CostTypeConverter::map).collect(Collectors.toList());
        return ResponseUtils.convertListToJson(typeViewList);
    }

    /**
     * @return список типов расходов
     */
    @PostMapping(value = "getTypes")
    public @ResponseBody
    ResponseEntity<List<CostTypeView>> getTypes() {
        List<CostTypeView> typeViews = service.getTypes().stream().map(CostTypeConverter::map).collect(Collectors.toList());
        return new ResponseEntity<>(typeViews, HttpStatus.OK);
    }

    /**
     * Сохранение расхода
     *
     * @param dto расход
     * @return сообщение
     */
    @PostMapping(value = "saveCost")
    public String saveCost(CostView dto, Model map, HttpServletResponse response) {
        try {
            Cost cost = CostConverter.fromView(dto);
            service.saveCost(cost);
            LOGGER.info("Запись о расходе: " + cost.getType().getName() + " произведена");
            map.addAttribute("message", "Запись о расходе произведена");
            return "success";
        } catch (Exception e) {
            map.addAttribute("message", "Ошибка по работе с БД! Попробуйте снова выбрать тип.");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Изменение типа расхода
     *
     * @param typeView тип расхода
     * @return сообщение
     */
    @PostMapping(value = "saveType")
    public String saveCostType(CostTypeView typeView, Model map, HttpServletResponse response) {
        try {
            CostType type = CostTypeConverter.fromView(typeView);
            if (!service.existType(type)) {
                service.saveType(type);
                LOGGER.info("Запись о типе расхода: " + typeView.getName() + " произведена");
                map.addAttribute("message", "Запись о типе расхода изменена");
                return "success";
            } else {
                map.addAttribute("message", "Название типа расхода должно быть уникальным.");
                response.setStatus(409);
                return "error";
            }
        } catch (Exception e) {
            map.addAttribute("message", "Ошибка по работе с БД!");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Удаление расхода
     *
     * @param id       ID расхода
     * @param map      Model
     * @param response ответ
     * @return сообщение о результате удаления расхода из базы
     */
    @PostMapping(value = "deleteCost/{id}")
    public String deleteCost(@PathVariable("id") Integer id, Model map, HttpServletResponse response) {
        try {
            service.delete(id);
            LOGGER.info("Расход удален!");
            map.addAttribute("message", "Расход удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить расход");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Удаление типа расхода
     *
     * @param id       ID типа расхода
     * @param map      Model
     * @param response ответ
     * @return сообщение о результате удаления расхода из базы
     */
    @PostMapping(value = "deleteCostType/{id}")
    public String deleteCostType(@PathVariable("id") Integer id, Model map, HttpServletResponse response) {
        try {
            service.deleteType(id);
            LOGGER.info("Тип расхода удален!");
            map.addAttribute("message", "Тип расхода удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить тип расхода");
            response.setStatus(409);
            return "error";
        }
    }
}
