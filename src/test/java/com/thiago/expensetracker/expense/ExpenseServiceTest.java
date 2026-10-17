package com.thiago.expensetracker.expense;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;

    @Test
    void create_shouldKeepCategory_whenCategoryIsProvided() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest(
                "Tesco groceries", new BigDecimal("45.90"), LocalDate.of(2026, 10, 15), Category.FOOD);
        when(repository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExpenseResponse response = service.create(request);

        // Assert
        assertThat(response.category()).isEqualTo(Category.FOOD);
        assertThat(response.description()).isEqualTo("Tesco groceries");
        assertThat(response.amount()).isEqualByComparingTo("45.90");
    }

    @Test
    void create_shouldDefaultToOther_whenCategoryIsMissing() {
        ExpenseRequest request = new ExpenseRequest(
                "Train to Oxford", new BigDecimal("8.50"), LocalDate.of(2026, 10, 14), null);
        when(repository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isEqualTo(Category.OTHER);
    }

    @Test
    void findAll_shouldQueryWholeMonth_whenMonthIsProvided() {
        service.findAll(YearMonth.of(2026, 2));

        verify(repository).findByDateBetweenOrderByDateDesc(
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
        verify(repository, never()).findAllByOrderByDateDesc();
    }

    @Test
    void findById_shouldThrow_whenExpenseDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_shouldThrowAndNotDelete_whenExpenseDoesNotExist() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ExpenseNotFoundException.class);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void update_shouldChangeFields_whenExpenseExists() {
        // Arrange
        Expense existing = new Expense(
                "Old description", new BigDecimal("10.00"), LocalDate.of(2026, 10, 1), Category.OTHER);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        ExpenseRequest request = new ExpenseRequest(
                "Tesco weekly shop", new BigDecimal("52.30"), LocalDate.of(2026, 10, 15), Category.FOOD);

        // Act
        ExpenseResponse response = service.update(1L, request);

        // Assert
        assertThat(response.description()).isEqualTo("Tesco weekly shop");
        assertThat(response.amount()).isEqualByComparingTo("52.30");
        assertThat(response.category()).isEqualTo(Category.FOOD);
    }
}