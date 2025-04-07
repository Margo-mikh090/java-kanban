package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class PriorityListHandler extends BaseHttpHandler implements HttpHandler {

    public PriorityListHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (!method.equals("GET") || !Pattern.matches("^/prioritized$", requestPath)) {
            sendBadRequest(exchange, "Данный запрос не может быть обработан." +
                    " Повторите попытку, изменив запрос");
            return;
        }
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendJson(exchange, gson.toJson(prioritizedTasks));
    }
}
