package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kircoop.gk23.converter.PaymentConverter;
import ru.kircoop.gk23.dto.PaymentView;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Payment;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.PaymentService;
import ru.kircoop.gk23.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentConverter converter;

    @Autowired
    private GaragService garagService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    /**
     * Страница с чеками определенного года
     *
     * @param year Год
     * @param map  ModelMap
     * @return страница payments.jsp
     */
    @GetMapping(value = "paymentsPage")
    public String getPaymentsPage(@RequestParam(required = false, value = "year") Integer year, Model map) {
        try {
            map.addAttribute("setYear", (year == null) ? Calendar.getInstance().get(Calendar.YEAR) : year);
            map.addAttribute("years", paymentService.findYears());
            return "payments";
        } catch (DataAccessException e) {
            map.addAttribute("textError", "Ошибка базы данных, проверте подключение к БД");
            return "errorPage";
        }
    }

    /**
     * Список всех платежей определенного года
     *
     * @param year Год
     * @return json список платежей
     */
    @GetMapping(value = "payments")
    public ResponseEntity<String> getPayments(@RequestParam("setYear") Integer year) {
        List<PaymentView> viewList = paymentService.findByYear(year).stream().map(converter::map).collect(Collectors.toList());
        return ResponseUtils.convertListToJson(viewList);
    }

    /**
     * Модальное окно платежа
     *
     * @param id   ID Гаража
     * @param type Тип платежа (основной/дополнительный)
     * @param map  ModelMap
     * @return modalPay.jsp
     */
    @GetMapping(value = "payModal")
    public String payModal(@RequestParam("idGarag") Integer id,
                           @RequestParam("type") String type,
                           Model map) {
        PaymentView payment = new PaymentView();
        payment.setGaragId(id);
        map.addAttribute("garagName", garagService.getGarag(id).getFullName());
        map.addAttribute("type", type);
        map.addAttribute("payment", payment);
        return "modalPay";
    }

    /**
     * Сохранение платежа
     *
     * @param paymentView Платеж
     * @param type        Тип платежа
     * @return номер проведенного платежа
     */
    @PostMapping(value = "savePayment")
    @ResponseBody
    public Integer savePayment(PaymentView paymentView, @RequestParam("type") String type, Model map, HttpServletResponse response) {
        try {
            Garag garag = garagService.getGarag(paymentView.getGaragId());
            if (garag != null) {
                Payment payment = converter.fromView(paymentView, garag);
                payment = paymentService.pay(payment, false, type);
                LOGGER.info("Оплата по гаражу:" + payment.getGarag().getName() + " произведена");
                return payment.getId();
            }
            LOGGER.error("Не найден гараж по идентификатору {}", paymentView.getGaragId());
            map.addAttribute("message", "Не найден гараж!");
            response.setStatus(409);
            return -1;
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить платеж");
            response.setStatus(409);
            return -1;
        }

    }

    /**
     * Печать выбранного чека
     *
     * @param id  ID чека
     * @param map ModelMap
     * @return страница с печатной формой чека order.jsp
     */
    @GetMapping(value = "printOrder/{id}")
    public String printOrder(@PathVariable("id") Integer id, Model map) {
        map.addAttribute("pay", converter.map(paymentService.getPayment(id)));
        return "order";
    }

    /**
     * Удаление платежа
     *
     * @param id       ID платежа
     * @param map      ModelMap
     * @param response ответ
     * @return сообщение о результате удаления платежа из базы
     */
    @RequestMapping(value = "deletePayment/{id}", method = RequestMethod.POST)
    public String deletePayment(@PathVariable("id") Integer id, Model map, HttpServletResponse response) {
        try {
            paymentService.delete(id);
            map.addAttribute("message", "Платеж удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить платеж");
            response.setStatus(409);
            return "error";
        }
    }
}
