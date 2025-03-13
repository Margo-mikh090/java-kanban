package server;

import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import managers.TaskManager;
import server.handlers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private final TaskManager taskManager;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PriorityListHandler(taskManager));
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер завершил работу");
    }
}
