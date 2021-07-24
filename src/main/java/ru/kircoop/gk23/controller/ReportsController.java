package ru.kircoop.gk23.controller;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kircoop.gk23.service.PaymentService;
import ru.kircoop.gk23.service.RentService;
import ru.kircoop.gk23.service.ReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.LocalDate;

import static ru.kircoop.gk23.utils.DateUtils.DD_MM_YYYY_DOT;

/**
 * Контроллер отчетов
 */
@Controller
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RentService rentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsController.class);

    /**
     * Страница дополнительных отчетов
     *
     * @param map ModelMap
     * @return report.jsp
     */
    @GetMapping(value = "reportOther")
    public String reportOther(Model map) {
        map.addAttribute("rents", rentService.getRents());
        map.addAttribute("years", paymentService.findYears());
        return "report";
    }

    /**
     * Отчет - общий список гаражей
     *
     * @param response
     * @param map
     * @return Отчет - общий список гаражей
     */
    @GetMapping(value = "reportAllPerson")
    public String reportAllPerson(HttpServletResponse response, Model map) {
        String fileName = fileName("Общий список");
        fileName += "(" + LocalDate.now().format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportAll(), fileName, response, map);
    }

    /**
     * Отчет -  список льготников
     *
     * @param response
     * @param map
     * @return Отчет -  список льготников
     */
    @GetMapping(value = "reportBenefitsPerson")
    public String reportBenefitsPerson(HttpServletResponse response, Model map) {
        String fileName = fileName("Список льготников");
        fileName += "(" + LocalDate.now().format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportBenefitsPerson(), fileName, response, map);
    }

    /**
     * Отчет -  список должников
     *
     * @param response
     * @param map
     * @return Отчет -  список должников
     */
    @GetMapping(value = "reportContribute")
    public String reportContribute(HttpServletResponse response, Model map) {
        String fileName = fileName("Список должников");
        fileName += "(" + LocalDate.now().format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportContribute(), fileName, response, map);
    }


    /**
     * Отчет -  доходы
     *
     * @param start Дата начала периода отчета
     * @param end   Дата завершения периода отчета
     * @param response
     * @param map
     * @return отчет
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping(value = "reportProfit")
    public String reportProfit(@RequestParam("profitDateStart") LocalDate start,
                               @RequestParam("profitDateEnd") LocalDate end,
                               HttpServletResponse response, Model map)  {
        String fileName = fileName("Отчет по доходам");
        fileName += "(" + start.format(DD_MM_YYYY_DOT) + "-" + end.format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportProfit(start, end), fileName, response, map);
    }

    /**
     * Отчет по платежам
     *
     * @param start    Дата начала периода отчета
     * @param end      Дата завершения периода отчета
     * @param response
     * @param map
     * @return отчет
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping(value = "reportPayments")
    public String reportPayments(@RequestParam("paymentDateStart") LocalDate start,
                                 @RequestParam("paymentDateEnd") LocalDate end,
                                 HttpServletResponse response, Model map) {
        String fileName = fileName("Отчет по платежам");
        fileName += "(" + start.format(DD_MM_YYYY_DOT) + "-" + end.format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportPayments(start, end), fileName, response, map);
    }

    /**
     * Отчет по типам расходов
     *
     * @param start    Дата начала периода отчета
     * @param end      Дата завершения периода отчета
     * @param response
     * @param map
     * @return отчет
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping(value = "reportGroupCost")
    public String reportGroupCost(@RequestParam("costTypeDateStart") LocalDate start,
                                  @RequestParam("costTypeDateEnd") LocalDate end,
                                  HttpServletResponse response, Model map) throws IOException, ParseException {
        String fileName = fileName("Отчет по типам расходов");
        fileName += "(" + start.format(DD_MM_YYYY_DOT) + "-" + end.format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportGroupCost(start, end), fileName, response, map);
    }

    /**
     * Отчет по расходам
     *
     * @param start    Дата начала периода отчета
     * @param end      Дата завершения периода отчета
     * @param response
     * @param map
     * @return отчет
     * @throws IOException
     * @throws ParseException
     */
    @GetMapping(value = "reportCost")
    public String reportCost(@RequestParam("reportCostDateStart") LocalDate start,
                             @RequestParam("reportCostDateEnd") LocalDate end,
                             HttpServletResponse response, Model map) throws IOException, ParseException {
        String fileName = fileName("Отчет по расходам");
        fileName += "(" + start.format(DD_MM_YYYY_DOT) + "-" + end.format(DD_MM_YYYY_DOT) + ").xls";
        return report(reportService.reportCost(start, end), fileName, response, map);
    }

    /**
     * Подготовка создания файла
     *
     * @param workbook excel
     * @param fileName имя файла
     * @param response ответ
     * @param map      мапа
     * @return ничего / страница с ошибкой
     */
    private String report(HSSFWorkbook workbook, String fileName,
                          HttpServletResponse response, Model map) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try {
            ServletOutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
            LOGGER.info("Сформирован отчет: " + fileName);
            return null;
        } catch (IOException e) {
            map.addAttribute("message", "Ошибка отправки отчета");
            response.setStatus(409);
            return "error";
        }
    }

    /**
     * Метод декодирования имя файла
     *
     * @param fileName имя файла
     * @return декодированное имя файла
     */
    private static String fileName(String fileName) {
        String encodeName;
        try {
            encodeName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return encodeName.replace('+', ' ');
    }


}
