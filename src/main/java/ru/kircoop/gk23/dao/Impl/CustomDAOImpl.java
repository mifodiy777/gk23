package ru.kircoop.gk23.dao.Impl;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Кирилл on 01.04.2017.
 */
@Repository
public class CustomDAOImpl {

    private static final String SUM_CONTRIBUTE = "SELECT (sum(c.contribute)+sum(c.cont_land)+sum(c.cont_target)+sum(c.fines))+g.old_contribute AS SUM " +
            "FROM contribution c INNER JOIN garag_contributions gc ON gc.contributions_id_count=c.id_count " +
            "INNER JOIN garag g ON gc.garag_id_garag=g.id_garag " +
            "WHERE gc.Garag_id_garag = :idGarag";

    @PersistenceContext
    private EntityManager em;

    /**
     * Метод вычисления суммы общего долга по определенному гаражу
     *
     * @param id Гаража
     * @return сумма
     */
    public Integer getSumContribution(Integer id) {
        return Integer.valueOf(em.createNativeQuery(SUM_CONTRIBUTE).setParameter("idGarag", id).getSingleResult().toString());
    }

}
