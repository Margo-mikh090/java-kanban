package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.TimeIntersectionException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(requestPath, method);

        switch (endpoint) {
            case GET_ALL -> handleGetAllTasks(exchange);
            case GET_BY_ID -> handleGetTaskById(exchange);
            case POST_ADD -> handleAddTask(exchange, body);
            case POST_UPDATE -> handleUpdateTask(exchange, body);
            case DELETE_ALL -> handleDeleteAllTasks(exchange);
            case DELETE_BY_ID -> handleDeleteTaskById(exchange);
            case UNKNOWN -> sendBadRequest(exchange, "Данный запрос не может быть обработан." +
                    " Повторите попытку, изменив запрос");
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        sendJson(exchange, gson.toJson(taskManager.getListOfTasks()));
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id задачи");
                return;
            }
            sendJson(exchange, gson.toJson(taskManager.getTask(idOpt.get())));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleAddTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = gson.fromJson(body, Task.class);
            taskManager.addTask(task);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные не соответствуют Task.class");
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleUpdateTask(HttpExchange exchange, String body) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id задачи");
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            taskManager.updateTask(task);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные не соответствуют Task.class");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllTasks();
            sendOK(exchange);
        } catch (ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id задачи");
                return;
            }
            taskManager.removeTask(idOpt.get());
            sendOK(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private Optional<Integer> getIdOpt(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> id;
        try {
            id = Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return id;
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("^/tasks$", requestPath)) return Endpoint.GET_ALL;
                if (Pattern.matches("^/tasks/\\d+$", requestPath)) return Endpoint.GET_BY_ID;
            case "POST":
                if (Pattern.matches("^/tasks$", requestPath)) return Endpoint.POST_ADD;
                if (Pattern.matches("^/tasks/\\d+$", requestPath)) return Endpoint.POST_UPDATE;
            case "DELETE":
                if (Pattern.matches("^/tasks$", requestPath)) return Endpoint.DELETE_ALL;
                if (Pattern.matches("^/tasks/\\d+$", requestPath)) return Endpoint.DELETE_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
