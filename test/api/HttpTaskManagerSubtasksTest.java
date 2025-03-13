package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationTypeAdapter;
import server.adapters.LocalDateTimeTypeAdapter;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void testAddAndUpdateSubtask() throws IOException, InterruptedException {
        URI subtasksUri = URI.create("http://localhost:8080/subtasks");
        URI subtasksWithId1 = URI.create("http://localhost:8080/subtasks/1");
        URI subtasksWithId2 = URI.create("http://localhost:8080/subtasks/2");

        String jsonEpic = gson.toJson(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        String jsonAddSubtask1 = gson.toJson(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "10.02.25 11:00", 180));
        String jsonUpdateSubtask1 = gson.toJson(new Subtask(2, "Test updateSubtask",
                "Test updateSubtask description", TaskStatus.NEW, 1, "10.02.25 11:00", 180));
        String jsonUpdateSubtask2 = gson.toJson(new Subtask(3, "Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "10.02.25 11:00", 180));

        HttpRequest requestAddEpic = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).build();
        client.send(requestAddEpic, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestAddSubtask1 = HttpRequest.newBuilder().uri(subtasksUri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonAddSubtask1)).build();
        HttpResponse<String> responseAddSubtask1 = client.send(requestAddSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseAddSubtask1.statusCode());
        Subtask addedSubtask1 = taskManager.getSubtask(2);
        assertNotNull(addedSubtask1);
        assertEquals("Test addNewSubtask", addedSubtask1.getTaskName());

        HttpRequest requestUpdateSubtask1 = HttpRequest.newBuilder().uri(subtasksWithId1)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateSubtask1)).build();
        HttpResponse<String> responseUpdateSubtask1 = client.send(requestUpdateSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseUpdateSubtask1.statusCode());
        Subtask updatedSubtask1 = taskManager.getSubtask(2);
        assertNotEquals(addedSubtask1.getTaskName(), updatedSubtask1.getTaskName());

        HttpRequest requestAddSubtask2 = HttpRequest.newBuilder().uri(subtasksUri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonAddSubtask1)).build();
        HttpResponse<String> responseAddSubtask2 = client.send(requestAddSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseAddSubtask2.statusCode());

        HttpRequest requestUpdateSubtask2 = HttpRequest.newBuilder().uri(subtasksWithId2)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateSubtask2)).build();
        HttpResponse<String> responseUpdateSubtask2 = client.send(requestUpdateSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseUpdateSubtask2.statusCode());
    }

    @Test
    public void testDeleteSubtasksAllAndById() throws IOException, InterruptedException {
        URI subtasksUri = URI.create("http://localhost:8080/subtasks");
        URI subtasksWithId4 = URI.create("http://localhost:8080/subtasks/4");

        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "09.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 08:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 14:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "09.02.25 08:00", 180));

        assertEquals(5, taskManager.getListOfSubtasks().size());
        HttpRequest requestDeleteSubtask4 = HttpRequest.newBuilder().uri(subtasksWithId4).DELETE().build();
        HttpResponse<String> responseDeleteSubtask4 = client.send(requestDeleteSubtask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteSubtask4.statusCode());
        assertEquals(4, taskManager.getListOfSubtasks().size());
        HttpResponse<String> responseDeleteSubtask4Twice = client.send(requestDeleteSubtask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseDeleteSubtask4Twice.statusCode());

        HttpRequest requestDeleteAllSubtasks = HttpRequest.newBuilder().uri(subtasksUri).DELETE().build();
        HttpResponse<String> responseDeleteAllSubtasks = client.send(requestDeleteAllSubtasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteAllSubtasks.statusCode());
        assertEquals(0, taskManager.getListOfSubtasks().size());
    }

    @Test
    public void testGetSubtaskAllAndById() throws IOException, InterruptedException {
        URI subtasksUri = URI.create("http://localhost:8080/subtasks");
        URI subtasksWithId4 = URI.create("http://localhost:8080/subtasks/4");
        URI subtasksWithId7 = URI.create("http://localhost:8080/subtasks/7");

        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "09.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 08:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 14:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "09.02.25 08:00", 180));

        String jsonSubtask4 = gson.toJson(new Subtask(4, "Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 08:00", 180));
        String jsonAllSubtasks = gson.toJson(taskManager.getListOfSubtasks());

        HttpRequest requestGetSubtask4 = HttpRequest.newBuilder().uri(subtasksWithId4).GET().build();
        HttpResponse<String> responseGetSubtask4 = client.send(requestGetSubtask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(jsonSubtask4, responseGetSubtask4.body());
        assertEquals(200, responseGetSubtask4.statusCode());

        HttpRequest requestGetSubtask6 = HttpRequest.newBuilder().uri(subtasksWithId7).GET().build();
        HttpResponse<String> responseGetSubtask6 = client.send(requestGetSubtask6, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetSubtask6.statusCode());

        HttpRequest requestGetAllSubtasks = HttpRequest.newBuilder().uri(subtasksUri).GET().build();
        HttpResponse<String> responseGetAllSubtasks = client.send(requestGetAllSubtasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetAllSubtasks.statusCode());
        assertEquals(jsonAllSubtasks, responseGetAllSubtasks.body());
    }

    @Test
    public void testWrongEndpoint() throws IOException, InterruptedException {
        URI subtasksUri1 = URI.create("http://localhost:8080/subtasks");
        URI subtasksUri2 = URI.create("http://localhost:8080/subtasks/34per9");
        URI subtasksUri3 = URI.create("http://localhost:8080/subtasks/five");

        String json = gson.toJson(new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", TaskStatus.NEW, 1, "08.02.25 08:00", 180));
        HttpRequest requestPut = HttpRequest.newBuilder().uri(subtasksUri1).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> responsePut = client.send(requestPut, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responsePut.statusCode());

        HttpRequest requestGet34per9 = HttpRequest.newBuilder().uri(subtasksUri2).GET().build();
        HttpResponse<String> responseGet34per9 = client.send(requestGet34per9, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGet34per9.statusCode());

        HttpRequest requestGetFiveSubtask = HttpRequest.newBuilder().uri(subtasksUri3).GET().build();
        HttpResponse<String> responseGetFiveTask = client.send(requestGetFiveSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGetFiveTask.statusCode());
    }
}
