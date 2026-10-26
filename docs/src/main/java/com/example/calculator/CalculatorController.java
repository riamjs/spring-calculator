package com.example.calculator;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
// Reads calculator.allowed-origin if set, otherwise falls back to localhost:5500.
@CrossOrigin(origins = "${calculator.allowed-origin:http://localhost:5500}")
public class CalculatorController {

    public record CalculateRequest(BigDecimal a, BigDecimal b, String operation) {}
    public record CalculateResponse(BigDecimal result) {}

    private final CalculatorService calculatorService;

    // Spring sees this constructor and passes in the @Service for us (dependency injection).
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    // @RequestBody turns the JSON into a CalculateRequest; the return value becomes JSON.
    @PostMapping("/calculate")
    public CalculateResponse calculate(@RequestBody CalculateRequest request) {
        BigDecimal result = calculatorService.calculate(request.operation(), request.a(), request.b());
        return new CalculateResponse(result);
    }

    // Divide by zero, bad operation and missing numbers become a 400 with a friendly message.
    @ExceptionHandler({IllegalArgumentException.class, ArithmeticException.class})
    public ResponseEntity<Map<String, String>> handleBadInput(RuntimeException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", error.getMessage()));
    }

    // Spring throws this when a or b isn't a number (e.g. "abc") or the body isn't valid JSON.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "We couldn't read that. Check that a and b are valid numbers."));
    }
}