package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.entity.HistoryGarag;

@Repository
public interface HistoryGaragDAO extends JpaRepository<HistoryGarag, Integer> {

}