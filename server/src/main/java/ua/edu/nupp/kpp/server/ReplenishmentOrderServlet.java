package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReplenishmentOrderServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ReplenishmentOrderServlet.class.getName());
    private final ReplenishmentOrderService service = new ReplenishmentOrderService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String action = req.getParameter("action");
            if (action != null && !"list".equals(action)) {
                throw new IllegalArgumentException("Невідома операція: " + action);
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                service.delete(id(req));
            } else if ("add".equals(action)) {
                service.add(longParam(req, "stockId"));
            } else if ("update".equals(action)) {
                service.update(id(req), longParam(req, "stockId"));
            } else if ("transition".equals(action)) {
                service.changeState(id(req), state(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;
        int status = clientError
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message;
        if (clientError) {
            message = e.getMessage() == null ? "Некоректний запит" : e.getMessage();
            LOG.warning("Запит відхилено: " + message);
        } else {
            message = "Внутрішня помилка сервера";
            LOG.log(Level.SEVERE, "Помилка під час обробки запиту", e);
        }
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private long id(HttpServletRequest req) {
        return longParam(req, "id");
    }

    private static long longParam(HttpServletRequest req, String name) {
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

    private static OrderState state(HttpServletRequest req) {
        String value = req.getParameter("state");
        try {
            return OrderState.valueOf(value);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Невідомий стан: " + value);
        }
    }
}
