package com.thiago.expensetracker.expense;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService service;

    @Test
    void create_shouldReturn201WithLocation_whenRequestIsValid() throws Exception {
        ExpenseResponse created = new ExpenseResponse(
                1L, "Tesco groceries", new BigDecimal("45.90"),
                LocalDate.of(2026, 10, 15), Category.FOOD, Instant.parse("2026-10-15T10:00:00Z"));
        when(service.create(any(ExpenseRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Tesco groceries",
                                  "amount": 45.90,
                                  "date": "2026-10-15",
                                  "category": "FOOD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/expenses/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("FOOD"));
    }

    @Test
    void create_shouldReturn400WithFieldErrors_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "",
                                  "amount": -5,
                                  "date": "2030-01-01"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.errors.amount").exists())
                .andExpect(jsonPath("$.errors.date").exists());

        verify(service, never()).create(any());
    }

    @Test
    void findById_shouldReturn404_whenExpenseDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new ExpenseNotFoundException(99L));

        mockMvc.perform(get("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Expense not found"));
    }

    @Test
    void findAll_shouldPassMonthToService_whenMonthParamIsProvided() throws Exception {
        mockMvc.perform(get("/api/expenses").param("month", "2026-10"))
                .andExpect(status().isOk());

        verify(service).findAll(YearMonth.of(2026, 10));
    }

    @Test
    void delete_shouldReturn204_whenExpenseExists() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenExpenseDoesNotExist() throws Exception {
        doThrow(new ExpenseNotFoundException(99L)).when(service).delete(99L);
        mockMvc.perform(delete("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Expense not found"));

        verify(service).delete(99L);
    }
}