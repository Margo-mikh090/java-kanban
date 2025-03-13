package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import server.adapters.DurationTypeAdapter;
import server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String message, int code) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, 0);
        exchange.getResponseBody().write(message.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendOK(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 400);
    }

    protected void sendError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 500);
    }

    protected void sendJson(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().write(json.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }
}
