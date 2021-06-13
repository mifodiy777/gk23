package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.entity.Rent;

@Repository
public interface RentDAO extends JpaRepository<Rent, Integer> {

    /**
     * Нахождение определенного начисления по году
     *
     * @param year год
     * @return начисление определенного года
     */
    Rent findByYearRent(Integer year);

}
