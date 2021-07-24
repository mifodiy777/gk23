package ru.kircoop.gk23;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.kircoop.gk23.service.ContributionService;

import java.util.Calendar;

/**
 * Класс обновления пеней при запуске приложения
 */
public class InitFines {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitFines.class);

    @Autowired
    private ContributionService service;

    /**
     * При инициализации контекста запускаетс метод обновления пеней
     */
    public void initFines() {
        try {
            service.onFines(Calendar.getInstance());
            service.updateFines();
            LOGGER.info("Обновление данных произведено");
        } catch (DataIntegrityViolationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
