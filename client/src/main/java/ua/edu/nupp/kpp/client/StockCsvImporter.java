package ua.edu.nupp.kpp.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class StockCsvImporter {
    private static final Logger LOG = Logger.getLogger(StockCsvImporter.class.getName());
    public static final String HEADER = "product_id;current_quantity;min_threshold;f2;f3";
    public static final int COLUMNS = 5;

    private StockCsvImporter() {
    }
    /** Підсумок імпорту: скільки записів прийнято і які рядки пропущено (з причиною). */
    public static final class Result {
        private int imported;
        private final List<String> skipped = new ArrayList<>();

        public int getImported() {
            return imported;
        }

        public List<String> getSkipped() {
            return skipped;
        }
    }

    public static Result importFile(Path file, HttpReplenishmentClient api) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !HEADER.equals(clean(lines.get(0)).trim())) {
            throw new IllegalArgumentException("Некоректний заголовок CSV. Очікується: " + HEADER);
        }
        Result result = new Result();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                api.addStock(parseLine(line));
                result.imported++;
            } catch (RuntimeException e) {
                String reason = "рядок " + (i + 1) + ": " + e.getMessage();
                result.skipped.add(reason);
                LOG.warning("Пропущено " + reason);
            }
        }
        return result;
    }
    /** Розбір одного рядка. Діапазони значень перевіряє сервер (StockService), а не клієнт. */
    public static Stock parseLine(String line) {
        String[] p = line.split(";", -1);
        if (p.length != COLUMNS) {
            throw new IllegalArgumentException("очікується " + COLUMNS + " полів, знайдено " + p.length);
        }
        return new Stock(null,
                parseLong(p[0], "product_id"),
                parseDouble(p[1], "current_quantity"),
                parseDouble(p[2], "min_threshold"),
                parseDouble(p[3], "f2"),
                parseDouble(p[4], "f3"));
    }

    private static long parseLong(String value, String field) {
        try {
            return Long.parseLong(required(value, field));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("поле " + field + " має бути цілим числом");
        }
    }

    private static double parseDouble(String value, String field) {
        try {
            double d = Double.parseDouble(required(value, field).replace(',', '.'));
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new NumberFormatException();
            }
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("поле " + field + " має бути числом");
        }
    }

    private static String required(String value, String field) {
        String v = value.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("поле " + field + " порожнє");
        }
        return v;
    }

    private static String clean(String s) {
        return s.startsWith("\uFEFF") ? s.substring(1) : s;
    }
}
