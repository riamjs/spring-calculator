package com.example.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    @Test
    void addsTwoNumbers() {
        assertEquals(new BigDecimal("16"), service.calculate("add", new BigDecimal("12"), new BigDecimal("4")));
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(new BigDecimal("3"), service.calculate("divide", new BigDecimal("12"), new BigDecimal("4")));
    }

    @Test
    void refusesToDivideByZero() {
        ArithmeticException error = assertThrows(ArithmeticException.class,
                () -> service.calculate("divide", new BigDecimal("5"), BigDecimal.ZERO));
        assertEquals("You can't divide by zero.", error.getMessage());
    }

    @Test
    void handlesDecimalsWithoutFloatingPointNoise() {
        // With doubles, 0.1 + 0.2 gives 0.30000000000000004
        assertEquals(new BigDecimal("0.3"), service.calculate("add", new BigDecimal("0.1"), new BigDecimal("0.2")));
    }

    @Test
    void convertsToPercent() {
        assertEquals(new BigDecimal("0.5"), service.calculate("percent", new BigDecimal("50"), null));
    }
}