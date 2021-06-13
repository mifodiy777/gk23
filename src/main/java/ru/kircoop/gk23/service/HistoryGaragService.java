package ru.kircoop.gk23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.HistoryGaragDAO;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.HistoryGarag;

import java.time.LocalDateTime;

@Service
public class HistoryGaragService {

    @Autowired
    private HistoryGaragDAO historyGaragDAO;

    /**
     * Получение определенной записи о сменах владельца гаража
     *
     * @param id ID Записи
     * @return запись истории
     */
    public HistoryGarag getHistoryGarag(Integer id) {
        return historyGaragDAO.getById(id);
    }

    /**
     * Сохранение записи о причине смены владельца
     *
     * @param reason Причина
     * @param fio    ФИО
     * @param garag  Гараж
     * @return запись истории о смене владельца
     */
    @Transactional
    public HistoryGarag saveReason(String reason, String fio, Garag garag) {
        return historyGaragDAO.save(new HistoryGarag(LocalDateTime.now(), fio, reason, garag));
    }

    /**
     * Удаление записи истории
     *
     * @param id Запись
     */
    @Transactional
    public void delete(Integer id) {
        historyGaragDAO.deleteById(id);
    }
}
