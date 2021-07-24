package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.entity.CostType;

/**
 * Класс по работе с базой данных через объект CostType
 */
@Repository
public interface CostTypeDAO extends JpaRepository<CostType, Integer> {

}
