package ua.edu.nupp.kpp.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StockServlet extends JsonServlet {
    private final StockService service = new StockService();

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
                service.add(read(req, null));
            } else if ("update".equals(action)) {
                service.update(read(req, longParam(req, "id")));
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

    private Stock read(HttpServletRequest req, Long id) {
        return new Stock(id, longParam(req, "productId"), doubleParam(req, "currentQuantity"),
                doubleParam(req, "minThreshold"), doubleParam(req, "f2"), doubleParam(req, "f3"));
    }
}