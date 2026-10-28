package com.thiago.expensetracker.expense;

import com.thiago.expensetracker.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ExpenseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createdExpense_shouldBePersistedAndRetrievable() throws Exception {
        String location = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Train to Oxford",
                                  "amount": 8.50,
                                  "date": "2026-10-14"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Train to Oxford"))
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void findAll_shouldReturnOnlyExpensesFromRequestedMonth_newestFirst() throws Exception {
        repository.save(new Expense("Last day of August", new BigDecimal("10.00"), LocalDate.of(2026, 8, 31), Category.FOOD));
        repository.save(new Expense("First day of September", new BigDecimal("20.00"), LocalDate.of(2026, 9, 1), Category.FOOD));
        repository.save(new Expense("Last day of September", new BigDecimal("30.00"), LocalDate.of(2026, 9, 30), Category.FOOD));
        repository.save(new Expense("First day of October", new BigDecimal("40.00"), LocalDate.of(2026, 10, 1), Category.FOOD));

        mockMvc.perform(get("/api/expenses").param("month", "2026-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value("2026-09-30"))
                .andExpect(jsonPath("$[1].date").value("2026-09-01"));
    }
}