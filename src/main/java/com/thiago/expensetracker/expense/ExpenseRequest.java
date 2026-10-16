package com.thiago.expensetracker.expense;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(@NotBlank @Size(max = 255) String description,
                             @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal amount,
                             @NotNull @PastOrPresent LocalDate date,
                             Category category) {

}
