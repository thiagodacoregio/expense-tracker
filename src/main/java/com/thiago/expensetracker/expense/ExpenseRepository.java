package com.thiago.expensetracker.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByOrderByDateDesc();

    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate start, LocalDate end);
    // findByDateBetweenOrderByDateDesc vira algo como WHERE expense_date BETWEEN ? AND ? ORDER BY expense_date DESC
}
