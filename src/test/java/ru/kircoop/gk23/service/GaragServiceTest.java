package ru.kircoop.gk23.service;

import com.cooperate.entity.Address;
import com.cooperate.entity.Garag;
import com.cooperate.entity.Person;
import com.cooperate.exception.ExistGaragException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.kircoop.gk23.dao.GaragDAO;
import ru.kircoop.gk23.dao.Impl.CustomDAOImpl;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Тестирование сервиса работы с гаражами
 * Created by Кирилл on 31.03.2017.
 */
public class GaragServiceTest {

    @Mock
    private GaragDAO garagDAO;

    @Mock
    private CustomDAOImpl customDAO;

    @Mock
    private PersonService personService;

    @Mock
    private HistoryGaragService historyGaragService;

    @InjectMocks
    private GaragService service = new GaragService();


    @BeforeClass
    public void init() throws Exception {
        initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        reset(garagDAO);
    }

    /**
     * Сохранение гаража с формы UI
     * Case: гараж существует
     *
     * @throws Exception
     */
    @Test(expectedExceptions = ServiceException.class)
    public void testSaveOrUpdateExcept() throws Exception {
        Garag garag = new Garag();
        garag.setSeries("1");
        garag.setNumber("1");
        given(customDAO.existGarag(garag)).willReturn(Boolean.TRUE);
        service.saveOrUpdate(garag);
    }

    /**
     * Сохранение гаража с формы UI
     * case: пустой гаража
     *
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdate() throws Exception {
        Garag garag = new Garag();
        garag.setSeries("1");
        garag.setNumber("1");
        given(customDAO.existGarag(garag)).willReturn(Boolean.FALSE);
        service.saveOrUpdate(garag);
        verify(garagDAO).save(garag);
    }

    /**
     * Сохранение гаража с формы UI
     * case: Сохранение гаража, владелец существова(производился поиск из базы)
     *
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdateIsNewPersonIsOld() throws Exception {
        Garag garag = spy(new Garag());
        Person person = new Person();
        person.setId(1);
        garag.setPerson(person);
        given(customDAO.existGarag(garag)).willReturn(Boolean.FALSE);
        given(garagDAO.save(garag)).willReturn(garag);
        service.saveOrUpdate(garag);
    }

    /**
     * Изменение гаража с формы UI
     * case: Гараж существовал, владелец существовал
     *
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdateIsNew() throws Exception {
        Garag garag = spy(new Garag());
        garag.setId(1);
        Person person = new Person();
        person.setId(1);
        garag.setPerson(person);
        given(garagDAO.findOne(garag.getId())).willReturn(garag);
        service.saveOrUpdate(garag);
        verify(garag).setContributions(null);
        verify(garag).setPayments(null);
        verify(garag).setHistoryGarags(null);
    }

    /**
     * Сохранение гаража полученного из базы
     *
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        Garag garag = new Garag();
        service.save(garag);
        verify(garagDAO).save(garag);
    }

    /**
     * Удаление гаража
     *
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        Integer id = 1;
        service.delete(id);
        verify(garagDAO).delete(id);
    }

    /**
     * Получение всех гаражей
     *
     * @throws Exception
     */
    @Test
    public void testGetGarags() throws Exception {
        service.getGarags();
        verify(garagDAO).findAll();
    }

    /**
     * Получение всех рядов
     *
     * @throws Exception
     */
    @Test
    public void testGetSeries() throws Exception {
        service.getSeries();
        verify(garagDAO).getSeries();
    }

    /**
     * Получение всех гаражей определенного ряда
     *
     * @throws Exception
     */
    @Test
    public void testFindBySeries() throws Exception {
        String series = "1";
        service.findBySeries(series);
        verify(garagDAO).findBySeries(series);
    }

    /**
     * Получение гаража по идентификатору
     *
     * @throws Exception
     */
    @Test
    public void testGetGarag() throws Exception {
        Integer id = 1;
        service.getGarag(id);
        verify(garagDAO).findOne(id);
    }

    /**
     * Получение общей суммы задолжности определенного гаража
     *
     * @throws Exception
     */
    @Test
    public void testSumContribution() throws Exception {
        Garag garag = new Garag();
        garag.setId(1);
        service.sumContribution(garag);
        verify(customDAO).getSumContribution(garag.getId());
    }

    /**
     * Case: Поиска владельца из базы не было, Старого владельца не удалять.
     * У Владельца был один гараж
     *
     * @throws Exception
     */
    @Test
    public void testChangePersonOneGaragNotDeleteNotSearch() throws Exception {
        Garag garag = new Garag();
        Person oldPerson = mock(Person.class);
        given(oldPerson.getFIO()).willReturn("Иванов Иван Иваныч");
        garag.setPerson(oldPerson);
        Person newPerson = new Person();
        newPerson.setFatherName("FatherName");
        newPerson.setLastName("LastName");
        newPerson.setName("Name");
        newPerson.setId(1);
        Address address = new Address();
        address.setId(1);
        newPerson.setAddress(address);
        String reason = "Причина";
        service.changePerson(garag, newPerson, false, false, 1, true, reason);
        assertNull(newPerson.getId());
        assertNull(newPerson.getAddress().getId());
        verify(historyGaragService).saveReason(reason, "Иванов Иван Иваныч", garag);
        assertEquals(garag.getPerson(), newPerson);
        verify(garagDAO).save(garag);
    }

    /**
     * Case: Поиска владельца из базы не было, Старого владельца удалять.
     * У Владельца было много гаражей
     *
     * @throws Exception
     */
    @Test
    public void testChangePersonManyGaragDeleteNotSearch() throws Exception {
        Garag garag = new Garag();
        Person person = new Person();
        List<Garag> garagList = new ArrayList<>();
        garagList.add(garag);
        person.setId(100);
        person.setFatherName("FatherName");
        person.setLastName("LastName");
        person.setName("Name");
        person.setGaragList(garagList);
        garag.setPerson(person);
        String reason = "Причина";
        service.changePerson(garag, person, false, true, 1, false, reason);
        verify(historyGaragService).saveReason(reason, "LastName Name FatherName", garag);
        verify(personService).saveOrUpdate(person);
        assertNotNull(person.getId());
        assertNotNull(person.getGaragList());
        for (Garag g : person.getGaragList()) {
            assertEquals(person, g.getPerson());
        }
    }

    /**
     * Case: Поиска владельца из базы, Старого владельца удалять.
     * У Владельца было много гаражей
     *
     * @throws Exception
     */
    @Test
    public void testChangePersonManyGaragDeleteSearch() throws Exception {
        Garag garag = new Garag();

        Person oldPerson = new Person();
        List<Garag> garagList = new ArrayList<>();
        garagList.add(garag);
        oldPerson.setGaragList(garagList);
        oldPerson.setId(100);
        oldPerson.setFatherName("oldFatherName");
        oldPerson.setLastName("oldLastName");
        oldPerson.setName("oldName");
        garag.setPerson(oldPerson);

        Person newPerson = new Person();
        newPerson.setName("newPerson");

        String reason = "Причина";
        Integer oldPersonId = 100;
        given(personService.getPerson(oldPersonId)).willReturn(oldPerson);
        given(personService.saveOrUpdate(newPerson)).willReturn(newPerson);

        service.changePerson(garag, newPerson, true, true, oldPersonId, false, reason);

        verify(personService).getPerson(oldPersonId);
        verify(historyGaragService).saveReason(reason, "oldLastName oldName oldFatherName", garag);
        verify(personService).saveOrUpdate(newPerson);
        verify(garagDAO).save(garag);
        assertEquals(garag.getPerson(), newPerson);
        verify(personService).delete(oldPersonId);

    }
}