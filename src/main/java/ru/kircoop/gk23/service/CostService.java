package ru.kircoop.gk23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kircoop.gk23.dao.CostDAO;
import ru.kircoop.gk23.dao.CostTypeDAO;
import ru.kircoop.gk23.dao.Impl.CustomDAOImpl;
import ru.kircoop.gk23.dto.CostDTO;
import ru.kircoop.gk23.entity.Cost;
import ru.kircoop.gk23.entity.CostType;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис по работе с расходами
 */
@Service
public class CostService {

    @Autowired
    private CostDAO costDAO;

    @Autowired
    private CostTypeDAO typeDAO;

    @Autowired
    private CustomDAOImpl customDAO;

    /**
     * Метод получения списка типов расходов
     *
     * @return Список типов расходов
     */
    public List<CostType> getTypes() {
        return typeDAO.findAll();
    }

    /**
     * Метод добавления расходов
     *
     * @param cost расход
     * @return расход
     */
    @Transactional
    public Cost saveCost(Cost cost) throws DataAccessException {
        if (cost.getType().getId() != null) {
            //detached
            cost.setType(typeDAO.findById(cost.getType().getId()).orElse(null));
        }
        costDAO.save(cost);
        return cost;
    }

    /**
     * Метод получения типа расхода
     *
     * @param id идентифиактор типа расхода
     * @return тип расхода
     */
    public CostType getType(Integer id) {
        return typeDAO.getById(id);
    }

    /**
     * Метод сохраниения типа расхода
     *
     * @param type Тип расхода
     * @return тип расхода
     */
    @Transactional
    public CostType saveType(CostType type) {
        return typeDAO.save(type);
    }

    /**
     * Получить список всех расходов
     *
     * @return расходы
     */
    public List<Cost> getAll() {
        return costDAO.findAll();
    }

    /**
     * Удаление расхода
     *
     * @param id ID расхода
     */
    @Transactional
    public void delete(Integer id) {
        costDAO.deleteById(id);
    }

    /**
     * Удаление типа расхода
     *
     * @param id ID типа расхода
     */
    @Transactional
    public void deleteType(Integer id) {
        customDAO.deleteCostType(id);
    }

    /**
     * Получить список объектов для отчета по типу расхода за период
     *
     * @param start начало периода
     * @param end   конец периода
     * @return список CostDTO
     */
    public List<CostDTO> findGroupCost(LocalDate start, LocalDate end) {
        return customDAO.findGroupCost(start, end);
    }

    /**
     * Проверка на уникальность
     *
     * @param type Тип расхода
     * @return true - не уникально
     */
    public boolean existType(CostType type) {
        return customDAO.existCostType(type);
    }
}
