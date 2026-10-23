package com.coursework.server;

public class RiskCalculator {
    /**
     * Розрахунок F1 у діапазоні 0.100
     * Для складської системи F1 може характеризувати рівень дефіциту запасу відносно мінімального порогу
     */
    public static double factor1(double currentQuantity, double minThreshold) {
        if (minThreshold <= 0) {
            throw new IllegalArgumentException("Мінімальний поріг має бути більшим за 0");
        }
        if (currentQuantity < 0) {
            throw new IllegalArgumentException("Поточна кількість не може бути від'ємною");
        }
        if (currentQuantity >= minThreshold) {
            return 0.0;
        }
        // Обчислення відсотка дефіциту
        double deficitRatio = (minThreshold - currentQuantity) / minThreshold * 100.0;
        // значення в межах 0..100
        return Math.max(0.0, Math.min(100.0, deficitRatio));
    }
    //обрахунки
    public static double calculate(double f1, double f2, double f3) {
        validateFactor(f1, "F1");
        validateFactor(f2, "F2");
        validateFactor(f3, "F3");
        double result = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        // Округлення до двох знаків після коми
        return Math.round(result * 100.0) / 100.0;
    }
    private static void validateFactor(double value, String name) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(name + " має бути в діапазоні 0.100");
        }
    }
}