package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationTypeAdapter;
import server.adapters.LocalDateTimeTypeAdapter;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerPrioritizedTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    @Test
    public void testGetTaskHistory() throws IOException, InterruptedException {
        server.start();

        URI uri1 = URI.create("http://localhost:8080/prioritized");
        URI uri2 = URI.create("http://localhost:8080/prioritized/get");

        taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 11:00", 180));
        taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "09.02.25 11:00", 180));
        taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 08:00", 180));
        taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 14:00", 180));
        taskManager.addTask(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "09.02.25 08:00", 180));

        String json = gson.toJson(taskManager.getPrioritizedTasks());

        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(json, response1.body());
        assertEquals(200, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder().uri(uri1).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response3.statusCode());

        server.stop();
    }
}
