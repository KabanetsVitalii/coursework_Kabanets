package ua.edu.nupp.kpp.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * /orders: GET (читання списку) та POST (action=add | update | transition | delete).
 * F1, F2, F3 та riskIndex клієнт не передає: їх розраховує ReplenishmentOrderService.
 * Допустимість переходу стану перевіряє Service, а не Servlet.
 */
public class ReplenishmentOrderServlet extends JsonServlet {
    private final ReplenishmentOrderService service = new ReplenishmentOrderService();

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
            if ("add".equals(action)) {
                service.add(longParam(req, "stockId"));
            } else if ("update".equals(action)) {
                service.update(longParam(req, "id"), longParam(req, "stockId"));
            } else if ("transition".equals(action)) {
                service.changeState(longParam(req, "id"), state(req));
            } else if ("delete".equals(action)) {
                service.delete(longParam(req, "id"));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
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