package com.company.springApplication.rep;


import com.company.springApplication.model.Expenses;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ExpensesRepository extends CrudRepository<Expenses, Long> {
    List<Expenses> findAllByOrderByDate(Sort date);
    List<Expenses> findByDate(Date date);
    boolean existsByDate(Date newDate);

    @Transactional
    void deleteByDateIn(List<java.sql.Date> expenses);
}
