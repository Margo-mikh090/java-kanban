package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();

        if (!method.equals("GET") || !Pattern.matches("^/history$", requestPath)) {
            sendBadRequest(exchange, "Данный запрос не может быть обработан." +
                    " Повторите попытку, изменив запрос");
            return;
        }
        List<Task> history = taskManager.getHistory();
        sendJson(exchange, gson.toJson(history));
    }
}
