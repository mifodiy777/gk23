package ru.kircoop.gk23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.GaragDAO;
import ru.kircoop.gk23.dao.Impl.CustomDAOImpl;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.Person;

import java.util.List;

/**
 * Сервис по работе с гаражами
 * Created by KuzminKA on 17.06.2015.
 */
@Service
public class GaragService {

    @Autowired
    private GaragDAO garagDAO;

    @Autowired
    private CustomDAOImpl customDAO;

    @Autowired
    private HistoryGaragService historyGaragService;

    @Autowired
    private PersonService personService;

    /**
     * Метод добавления и редактирования гаражей в базе
     *
     * @param garag Гараж с заполненными данными
     * @return Сохраненный гараж с заполненным id
     */
    @Transactional
    public Garag saveOrUpdate(Garag garag) throws ExistGaragException {
        //Редактирование гаража
        if (!customDAO.existGarag(garag)) {
            //Гараж новый, а владелец взят из базы.
            if (garag.getId() == null && garag.getPerson() != null && garag.getPerson().getId() != null) {
                Person p = garag.getPerson();
                garag.setPerson(null);
                Garag getGarag = garagDAO.save(garag);
                getGarag.setPerson(p);
                return garagDAO.save(getGarag);
            }
            //Если гараж уже есть в базе, не трогаем Историю, Платежи, Периоды.
            if (garag.getId() != null) {
                Garag garagEdit = garagDAO.findOne(garag.getId());
                garag.setContributions(garagEdit.getContributions());
                garag.setPayments(garagEdit.getPayments());
                garag.setHistoryGarags(garagEdit.getHistoryGarags());
            }
            //Во всех остальных случиях просто сохраняем гараж.
            return garagDAO.save(garag);
        }
        throw new ExistGaragException();
    }

    /**
     * Метод сохранения гаража уже имеющегося в базе
     *
     * @param garag Гараж
     * @return Сохраненный гараж
     */
    @Transactional
    public Garag save(Garag garag) {
        return garagDAO.save(garag);
    }

    /**
     * Метод удаления гаража
     *
     * @param id ID гаража
     */
    @Transactional
    public void delete(Integer id) {
        garagDAO.deleteById(id);
    }

    /**
     * Метод получения всех гаражей
     *
     * @return Коллекция со всеми гаражами
     */
    public List<Garag> getGarags() {
        return garagDAO.findAll();
    }

    /**
     * Метод получения всех имеющихся рядов
     *
     * @return ряды гаражного кооператива
     */
    public List<String> getSeries() {
        return garagDAO.getSeries();
    }

    /**
     * Получение всех гаражей определенного ряда
     *
     * @param series Ряд
     * @return Коллекция гаражей
     */
    public List<Garag> findBySeries(String series) {
        return garagDAO.findBySeries(series);
    }

    /**
     * Получение гаража по идентификатору
     *
     * @param id ID
     * @return Гараж
     */
    public Garag getGarag(Integer id) {
        return garagDAO.findById(id).orElse(null);
    }

    /**
     * Метод вычисления суммы общего долга по определенному гаражу
     *
     * @param garag Гараж
     * @return Сумма долга
     */
    public Integer sumContribution(Garag garag) {
        return customDAO.getSumContribution(garag.getId());
    }

    /**
     * Метод смены владельца у гаража
     *
     * @param garag        Гараж
     * @param person       Новый владелец
     * @param searchPerson Производился ли поиск владельца из базы
     * @param deletePerson Будет ли удален предыдущий владелец
     * @param oldPersonId  ID предыдущего владельца
     * @param oneGarag     Один ли гараж у прошлого владельца
     * @param reason       Причина смены владельца
     */
    public void changePerson(Garag garag, Person person, Boolean searchPerson, Boolean deletePerson, Integer oldPersonId, Boolean oneGarag, String reason) {
        // Если поиск не производился(т.е. новый владелец не из базы и удалять его не надо)
        if (!searchPerson && !deletePerson) {
            //Очищаем id владельца
            person.setId(null);
            //Очищаем адрес
            person.getAddress().setId(null);
        }
        //Если гараж у данного владельца не один, владельца необходимо удалить, поиск
        if (!oneGarag && deletePerson && !searchPerson) {
            for (Garag g : garag.getPerson().getGaragList()) {
                historyGaragService.saveReason(reason, garag.getPerson().getFIO(), g);
            }
            personService.saveOrUpdate(person);
            return;
        }
        if (oneGarag) {
            historyGaragService.saveReason(reason, garag.getPerson().getFIO(), garag);
            garag.setPerson(person);
            save(garag);
        } else {
            Person oldPerson = personService.getPerson(oldPersonId);
            person = personService.saveOrUpdate(person);
            for (Garag g : oldPerson.getGaragList()) {
                historyGaragService.saveReason(reason, garag.getPerson().getFIO(), g);
                g.setPerson(person);
                save(g);
            }
        }
        if (searchPerson && deletePerson) {
            personService.delete(oldPersonId);
        }
    }
}

