package ua.edu.nupp.kpp.server;

public class RiskCalculator {
    /**
     * F1 — дефіцит запасу, нормований до діапазону 0..100:
     * F1 = max(0; min(100; (minThreshold - currentQuantity) / minThreshold * 100)).
     * Якщо поточний запас не менший за мінімальний поріг, дефіциту немає і F1 = 0.
     */
    public static double factor1(double currentQuantity, double minThreshold) {
        if (currentQuantity < 0 || minThreshold <= 0) {
            throw new IllegalArgumentException("Некоректні дані для F1");
        }
        double value = (minThreshold - currentQuantity) / minThreshold * 100.0;
        return Math.max(0.0, Math.min(100.0, value));
    }
    /** I = 0,40*F1 + 0,35*F2 + 0,25*F3, результат округлюється до двох знаків. */
    public static double calculate(double f1, double f2, double f3) {
        check(f1);
        check(f2);
        check(f3);
        return Math.round((0.40 * f1 + 0.35 * f2 + 0.25 * f3) * 100.0) / 100.0;
    }

    private static void check(double value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Фактор має бути в діапазоні 0..100");
        }
    }
}
