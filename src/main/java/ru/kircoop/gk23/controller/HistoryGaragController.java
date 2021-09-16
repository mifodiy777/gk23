package ru.kircoop.gk23.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kircoop.gk23.converter.HistoryConverter;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.HistoryGaragService;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@Controller
public class HistoryGaragController {

    @Autowired
    private HistoryGaragService historyGaragService;

    @Autowired
    private GaragService garagService;

    @Autowired
    private HistoryConverter historyConverter;


    /**
     * Информационно модальное окно с историей изменений владельцев гаража
     *
     * @param id  Гаража
     * @param map ModelMap
     * @return страница historyGarag.jsp
     */
    @GetMapping(value = "getHistoryGarag/{id}")
    public String historyModalGarag(@PathVariable("id") Integer id, Model map) {
        Garag garag = garagService.getGarag(id);
        if (garag != null) {
            map.addAttribute("history", garag.getHistoryGarags().stream().map(historyConverter::map).collect(Collectors.toList()));
        }
        return "historyGarag";
    }

    /**
     * Удаление записи об изменении владельца у текущего гаража
     *
     * @param idReason ID Записи об изменении владельца у гаража
     * @param map      ModelMap
     * @param response ответ
     * @return Сообщение о результате удаления записи
     */
    @RequestMapping(value = "deleteReason", method = RequestMethod.POST)
    public String deleteReason(@RequestParam("idReason") Integer idReason,
                               Model map, HttpServletResponse response) {
        try {
            historyGaragService.delete(idReason);
            map.addAttribute("message", "Запись о смене владельца удалена!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить!");
            response.setStatus(409);
            return "error";
        }
    }
}
