package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.entity.Garag;
import ru.kircoop.gk23.entity.HistoryGarag;

import java.util.List;

@Repository
public interface HistoryGaragDAO extends JpaRepository<HistoryGarag, Integer> {

    /**
     * Удалить список записей по смене владельца определенного гаража
     *
     * @param garag гараж
     */
    void deleteByGarag(Garag garag);

    /**
     * Список записей по смене владельца определенного гаража
     * @param garag Гараж
     * @return
     */
    List<HistoryGarag> findByGarag(Garag garag);

}