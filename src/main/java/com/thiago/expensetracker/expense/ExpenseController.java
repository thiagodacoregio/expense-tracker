package com.thiago.expensetracker.expense;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse created = service.create(request);
        URI location = URI.create("/api/expenses/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<ExpenseResponse> findAll(@RequestParam(required = false) YearMonth month) {
        return service.findAll(month);
    }

    @GetMapping("/{id}")
    public ExpenseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
