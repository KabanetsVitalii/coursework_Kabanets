package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public abstract class JsonServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(JsonServlet.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();
    /** Успішна відповідь: Java-об'єкт або список перетворюється у JSON (Java -> JSON). */
    protected void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }
    /**
     * Помилка у вигляді JSON {"error": "текст"}.
     * 400 — некоректний запит або порушення предметного правила (IllegalArgumentException),
     * 500 — збій бази даних чи внутрішня помилка. Сервер після помилки продовжує працювати.
     */
    protected void writeError(HttpServletResponse resp, Exception e) throws IOException {
        int status;
        String message;
        if (e instanceof IllegalArgumentException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = e.getMessage();
            LOG.warning("Запит відхилено: " + message);
        } else if (e instanceof SQLException) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            message = "Помилка бази даних: " + e.getMessage();
            LOG.log(Level.SEVERE, "Помилка бази даних", e);
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            message = "Внутрішня помилка сервера";
            LOG.log(Level.SEVERE, "Непередбачена помилка", e);
        }
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message == null ? "Помилка" : message));
    }

    /** Обов'язковий цілий параметр (id, зовнішні ключі). */
    protected static long longParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("Не вказано параметр " + name);
        }
        try {
            return Long.parseLong(v.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " має бути цілим числом");
        }
    }
    protected static int intParam(HttpServletRequest req, String name) {
        long v = longParam(req, name);
        if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Параметр " + name + " виходить за допустимі межі");
        }
        return (int) v;
    }
    /** Обов'язковий дробовий параметр; NaN та нескінченність відхиляються. */
    protected static double doubleParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("Не вказано параметр " + name);
        }
        try {
            double d = Double.parseDouble(v.trim());
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new NumberFormatException();
            }
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр " + name + " має бути числом");
        }
    }
}
