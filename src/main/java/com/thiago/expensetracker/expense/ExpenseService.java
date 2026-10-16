package com.thiago.expensetracker.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        // TODO: ask the AI to suggest a category (Stage 6)
        Category category = request.category() != null ? request.category() : Category.OTHER;

        Expense expense = new Expense(
                request.description(),
                request.amount(),
                request.date(),
                category
        );

        return ExpenseResponse.from(repository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAll(YearMonth month) {
        List<Expense> expenses = (month == null)
                ? repository.findAllByOrderByDateDesc()
                : repository.findByDateBetweenOrderByDateDesc(month.atDay(1), month.atEndOfMonth());

        return expenses.stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return repository.findById(id)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }
}