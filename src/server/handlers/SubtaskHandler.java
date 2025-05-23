package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.TimeIntersectionException;
import exceptions.UpdateEpicTimeException;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(requestPath, method);

        switch (endpoint) {
            case GET_ALL -> handleGetAllSubtasks(exchange);
            case GET_BY_ID -> handleGetSubtaskById(exchange);
            case POST_ADD -> handleAddSubtask(exchange, body);
            case POST_UPDATE -> handleUpdateSubtask(exchange, body);
            case DELETE_ALL -> handleDeleteAllSubtasks(exchange);
            case DELETE_BY_ID -> handleDeleteSubtaskById(exchange);
            case UNKNOWN -> sendBadRequest(exchange, "Данный запрос не может быть обработан." +
                    " Повторите попытку, изменив запрос");
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        sendJson(exchange, gson.toJson(taskManager.getListOfSubtasks()));
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        try {
            int id = getValidId(exchange);
            if (id != -1) sendJson(exchange, gson.toJson(taskManager.getSubtask(id)));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleAddSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            taskManager.addSubtask(subtask);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные невалидны для создания подзадачи");
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            int id = getValidId(exchange);
            if (id != -1) {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                taskManager.updateSubtask(subtask);
                sendCreated(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные невалидны для создания подзадачи");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllSubtasks();
            sendOK(exchange);
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        try {
            int id = getValidId(exchange);
            if (id != -1) {
                taskManager.removeSubtask(id);
                sendOK(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET":
                if (Pattern.matches("^/subtasks$", requestPath)) return Endpoint.GET_ALL;
                if (Pattern.matches("^/subtasks/\\d+$", requestPath)) return Endpoint.GET_BY_ID;
            case "POST":
                if (Pattern.matches("^/subtasks$", requestPath)) return Endpoint.POST_ADD;
                if (Pattern.matches("^/subtasks/\\d+$", requestPath)) return Endpoint.POST_UPDATE;
            case "DELETE":
                if (Pattern.matches("^/subtasks$", requestPath)) return Endpoint.DELETE_ALL;
                if (Pattern.matches("^/subtasks/\\d+$", requestPath)) return Endpoint.DELETE_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
