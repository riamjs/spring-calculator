package com.example.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import org.springframework.stereotype.Service;

// @Service tells Spring to create one instance and share it wherever it's needed.
@Service
public class CalculatorService {

    public BigDecimal calculate(String operation, BigDecimal a, BigDecimal b) {
        if (operation == null || operation.isBlank()) {
            throw new IllegalArgumentException("Please choose an operation.");
        }
        if (a == null) {
            throw new IllegalArgumentException("Please enter a number.");
        }

        BigDecimal result = switch (operation.toLowerCase()) {
            case "add" -> a.add(second(b));
            case "subtract" -> a.subtract(second(b));
            case "multiply" -> a.multiply(second(b));
            case "divide" -> divide(a, second(b));
            case "percent" -> a.divide(BigDecimal.valueOf(100));
            case "negate" -> a.negate();
            default -> throw new IllegalArgumentException("Unknown operation \"" + operation + "\".");
        };

        return tidy(result);
    }

    private BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new ArithmeticException("You can't divide by zero.");
        }
        // 1 / 3 never ends, so cap it at 16 significant digits
        return a.divide(b, MathContext.DECIMAL64);
    }

    private BigDecimal second(BigDecimal b) {
        if (b == null) {
            throw new IllegalArgumentException("This operation needs a second number.");
        }
        return b;
    }

    // stripTrailingZeros turns 100 into 1E+2, so put the scale back to 0 in that case.
    private BigDecimal tidy(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }
}