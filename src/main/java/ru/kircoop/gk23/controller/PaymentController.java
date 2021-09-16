package ru.kircoop.gk23.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kircoop.gk23.service.GaragService;
import ru.kircoop.gk23.service.PaymentService;
import ru.kircoop.gk23.service.RentService;

import javax.servlet.http.HttpServletResponse;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RentService rentService;

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
            map.addAttribute("rents", rentService.getRents());
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Payment.class, new PaymentAdapter());
        return Utils.convertListToJson(gsonBuilder, paymentService.findByYear(year));
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
        map.addAttribute("type", type);
        map.addAttribute("payment", new Payment(garagService.getGarag(id)));
        return "modalPay";
    }

    /**
     * Сохранение платежа
     *
     * @param payment Платеж
     * @param type    Тип платежа
     * @return номер проведенного платежа
     */
    @PostMapping(value = "savePayment")
    @ResponseBody
    public Integer savePayment(Payment payment, @RequestParam("type") String type) {
        payment = paymentService.pay(payment, false, type);
        LOGGER.info("Оплата по гаражу:" + payment.getGarag().getName() + " произведена");
        return payment.getId();
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
        map.addAttribute("pay", paymentService.getPayment(id));
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
            String garag = paymentService.getPayment(id).getGarag().getName();
            paymentService.delete(id);
            LOGGER.info("Платеж к гаражу " + garag + " удален!");
            map.addAttribute("message", "Платеж удален!");
            return "success";
        } catch (DataAccessException e) {
            map.addAttribute("message", "Невозможно удалить платеж");
            response.setStatus(409);
            return "error";
        }
    }
}
