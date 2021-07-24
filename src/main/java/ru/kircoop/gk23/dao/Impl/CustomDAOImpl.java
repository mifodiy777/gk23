package ru.kircoop.gk23.dao.Impl;

import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.dto.CostDTO;
import ru.kircoop.gk23.entity.CostType;
import ru.kircoop.gk23.entity.Person;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Кирилл on 01.04.2017.
 */
@Repository
public class CustomDAOImpl {

    private static final String SUM_CONTRIBUTE = "SELECT (sum(c.contribute)+sum(c.cont_land)+sum(c.cont_target)+sum(c.fines))+g.old_contribute AS SUM " +
            "FROM contribution c INNER JOIN garag_contributions gc ON gc.contributions_id_count=c.id_count " +
            "INNER JOIN garag g ON gc.garag_id_garag=g.id_garag " +
            "WHERE gc.Garag_id_garag = :idGarag";

    private static final String FIND_GROUP_COST = "SELECT t.name as type,COUNT(c.id) as count, sum(c.money) as sum from cost c INNER join costtype t " +
            "ON t.id=c.id_type WHERE c.date >= :start and c.date <= :end group by type";

    private static final String DELETE_COST = "delete from cost where id_type = :id";

    private static final String DELETE_TYPE = "delete from costtype where id = :id";

    private static final String PERSON_WITHOUT_GARAG = "SELECT * FROM Person p LEFT JOIN Garag g ON g.id_person = p.id_person WHERE g.id_person IS NULL";

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

    /**
     * Список членов без гаража
     *
     * @return список владельцев
     */
   public List<Person> findPersonWithoutGarag() {
        return (List<Person>) em.createNativeQuery(PERSON_WITHOUT_GARAG).getResultList();
    }

    /**
     * Получить список объектов для отчета по типу расхода за период
     *
     * @param start начало периода
     * @param end   конец периода
     * @return список CostDTO
     */
    public List<CostDTO> findGroupCost(LocalDate start, LocalDate end) {
        List<CostDTO> dtoList = em.createNativeQuery(FIND_GROUP_COST)
                .setParameter("start", start)
                .setParameter("end", end)
                .unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(CostDTO.class)).list();
        return dtoList;
    }

    /**
     * Удаление типа расхода
     *
     * @param id ID типа расхода
     */
    public void deleteCostType(Integer id) {
        em.createNativeQuery(DELETE_COST).setParameter("id", id).executeUpdate();
        em.createNativeQuery(DELETE_TYPE).setParameter("id", id).executeUpdate();
    }

    /**
     * Проверка на уникальность
     *
     * @param type Тип расхода
     * @return true - не уникально
     */
    public boolean existCostType(CostType type) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = builder.createQuery(Long.class);
        Root<CostType> root = cq.from(CostType.class);
        cq.select(builder.count(root));
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.notEqual(root.get("id"), type.getId()));
        predicates.add(builder.equal(root.get("name"), type.getName()));
        cq.where(predicates.toArray(new Predicate[]{}));
        return em.createQuery(cq).getSingleResult() > 0;
    }

}
