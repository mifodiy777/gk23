package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kircoop.gk23.entity.Payment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentDAO extends JpaRepository<Payment, Integer> {

    /**
     * Метод возвращает платежи с остаточными средствами для определенного гаража
     *
     * @param id Идентификатор гаража
     * @return список платежей
     */
    @Query("select p from Payment p where p.pay <> 0 and p.garag.id = :id order by p.datePayment DESC")
    List<Payment> getPaymentOnGaragNotNullPay(@Param("id") Integer id);

    /**
     * Метод возвращает платежи для определенного гаража
     *
     * @param id Идентификатор гаража
     * @return список платежей
     */
    @Query("select p from Payment p where p.garag.id = :id order by p.datePayment DESC")
    List<Payment> getPaymentOnGarag(@Param("id") Integer id);

    /**
     * Последний номер платежа
     *
     * @return номер
     */
    @Query("select max(p.number) from Payment p")
    Integer getMaxValueNumber();

    /**
     * Список годов для которых в базе существуют платежи
     *
     * @return
     */
    @Query("select distinct p.year from Payment p")
    List<Integer> findYears();

    /**
     * Список платежей определенного года
     *
     * @param year год
     * @return список платежей
     */
    List<Payment> findByYear(Integer year);

    /**
     * Список платежей в определенный промежуток времени
     *
     * @param start начало периода
     * @param end   конец периода
     * @return список платежей
     */
    @Query("select distinct p from Payment p where p.datePayment BETWEEN :start and :end")
    List<Payment> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Метод возвращает платежи гаражей конкретного ряда
     *
     * @param series Ряд гаража
     * @return список платежей
     */
    @Query("select p from Payment p inner join p.garag g where p.pay <> 0 and g.series = :series and p.datePayment BETWEEN :start and :end order by g.number ASC")
    List<Payment> findPaymentOnSeriesGaragBetweenDate(@Param("series") String series,
                                                      @Param("start") LocalDate start,
                                                      @Param("end") LocalDate end);

}
