package ru.kircoop.gk23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.Impl.CustomDAOImpl;
import ru.kircoop.gk23.dao.PersonDAO;
import ru.kircoop.gk23.entity.Person;

import java.util.List;

/**
 * Created by KuzminKA on 17.06.2015.
 */
@Service
public class PersonService {

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CustomDAOImpl customDAO;

    /**
     * Сохранение владельца
     *
     * @param person Владелец
     * @return владелец
     */
    @Transactional
    public Person saveOrUpdate(Person person) {
        return personDAO.saveAndFlush(person);
    }

    /**
     * Удаление владельца
     *
     * @param id Идентификатор владельца
     */
    @Transactional
    public void delete(Integer id) {
        personDAO.deleteById(id);
    }

    /**
     * Получение списка владельцев из базы по определенному паттерну или любые 30 владельцев
     *
     * @param fio Паттерны - часть ФИО
     * @return Список владельцев
     */
    public List<Person> getPersons(String fio) {
        if (fio != null && !fio.isEmpty()) {
            return personDAO.findByPersonfio(fio);
        }
        return personDAO.findTop30By();
    }

    /**
     * Получить список владельцев не имеющих гаражей в кооперативе
     * @return
     */
    public List<Person> getEmptyPersons() {
        return customDAO.findPersonWithoutGarag();
    }

    /**
     * Получение списка членов правления
     *
     * @return список членов правления
     */
    public List<Person> getMembers() {
        return personDAO.findByMemberBoard(true);
    }

    /**
     * Получение владельца по идентификатору
     *
     * @param id идентификатор владельца
     * @return владелец
     */
    public Person getPerson(Integer id) {
        return personDAO.findById(id).orElse(null);
    }

    /**
     * Получение списка владельце по части ФИО
     *
     * @param fio часть или полностью ФИО
     * @return Список владельцев
     */
    public List<Person> findByfio(String fio) {
        return personDAO.findByPersonfio(fio);
    }


}

