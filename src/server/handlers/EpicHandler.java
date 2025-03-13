package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.UpdateEpicTimeException;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = getEndpoint(requestPath, method);

        switch (endpoint) {
            case GET_ALL -> handleGetAllEpics(exchange);
            case GET_BY_ID -> handleGetEpicById(exchange);
            case GET_SUBLIST_BY_ID -> handleGetSubtasksByEpicId(exchange);
            case POST_ADD -> handleAddEpic(exchange, body);
            case POST_UPDATE -> handleUpdateEpic(exchange, body);
            case DELETE_ALL -> handleDeleteAllEpics(exchange);
            case DELETE_BY_ID -> handleDeleteEpicById(exchange);
            case UNKNOWN -> sendBadRequest(exchange, "Данный запрос не может быть обработан." +
                    " Повторите попытку, изменив запрос");
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        sendJson(exchange, gson.toJson(taskManager.getListOfEpics()));
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id эпика");
                return;
            }
            sendJson(exchange, gson.toJson(taskManager.getEpic(idOpt.get())));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleGetSubtasksByEpicId(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id эпика");
                return;
            }
            sendJson(exchange, gson.toJson(taskManager.getSubtasksByEpicID(idOpt.get())));
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleAddEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.addEpic(epic);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные не соответствуют Epic.class");
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id эпика");
                return;
            }
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.updateEpic(epic);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Переданные данные не соответствуют Epic.class");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (UpdateEpicTimeException | ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllEpics();
            sendOK(exchange);
        } catch (ManagerSaveException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        try {
            Optional<Integer> idOpt = getIdOpt(exchange);
            if (idOpt.isEmpty()) {
                sendBadRequest(exchange, "Некорректный id эпика");
                return;
            }
            taskManager.removeEpic(idOpt.get());
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
                if (Pattern.matches("^/epics$", requestPath)) return Endpoint.GET_ALL;
                if (Pattern.matches("^/epics/\\d+$", requestPath)) return Endpoint.GET_BY_ID;
                if (Pattern.matches("^/epics/\\d+/subtasks$", requestPath)) return Endpoint.GET_SUBLIST_BY_ID;
            case "POST":
                if (Pattern.matches("^/epics$", requestPath)) return Endpoint.POST_ADD;
                if (Pattern.matches("^/epics/\\d+$", requestPath)) return Endpoint.POST_UPDATE;
            case "DELETE":
                if (Pattern.matches("^/epics$", requestPath)) return Endpoint.DELETE_ALL;
                if (Pattern.matches("^/epics/\\d+$", requestPath)) return Endpoint.DELETE_BY_ID;
            default:
                return Endpoint.UNKNOWN;
        }
    }
}
