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
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
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
    public void testAddAndUpdateTask() throws IOException, InterruptedException {
        URI tasksUri = URI.create("http://localhost:8080/tasks");
        URI tasksWithId1 = URI.create("http://localhost:8080/tasks/1");
        URI tasksWithId2 = URI.create("http://localhost:8080/tasks/2");

        String jsonAddTask1 = gson.toJson(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "10.02.25 11:00", 180));
        String jsonUpdateTask1 = gson.toJson(new Task(1, "Test updateNewTask",
                "Test updateNewTask description", TaskStatus.IN_PROGRESS, "10.02.25 11:00", 180));
        String jsonUpdateTask2 = gson.toJson(new Task(2, "Test updateNewTask2",
                "Test updateNewTask description2", TaskStatus.IN_PROGRESS, "10.02.25 11:00", 180));

        HttpRequest requestAddTask1 = HttpRequest.newBuilder().uri(tasksUri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonAddTask1)).build();
        HttpResponse<String> responseAddTask1 = client.send(requestAddTask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseAddTask1.statusCode());
        Task addedTask1 = taskManager.getTask(1);
        assertNotNull(addedTask1);
        assertEquals("Test addNewTask", addedTask1.getTaskName());


        HttpRequest requestUpdateTask1 = HttpRequest.newBuilder().uri(tasksWithId1)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateTask1)).build();
        HttpResponse<String> responseUpdateTask1 = client.send(requestUpdateTask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseUpdateTask1.statusCode());
        Task updatedTask1 = taskManager.getTask(1);
        assertNotEquals(addedTask1.getTaskName(), updatedTask1.getTaskName());

        HttpRequest requestAddTask2 = HttpRequest.newBuilder().uri(tasksUri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonAddTask1)).build();
        HttpResponse<String> responseAddTask2 = client.send(requestAddTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseAddTask2.statusCode());

        HttpRequest requestUpdateTask2 = HttpRequest.newBuilder().uri(tasksWithId2)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateTask2)).build();
        HttpResponse<String> responseUpdateTask2 = client.send(requestUpdateTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseUpdateTask2.statusCode());
    }

    @Test
    public void testDeleteTasksAllAndById() throws IOException, InterruptedException {
        URI tasksUri = URI.create("http://localhost:8080/tasks");
        URI tasksWithId4 = URI.create("http://localhost:8080/tasks/4");

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

        assertEquals(5, taskManager.getListOfTasks().size());
        HttpRequest requestDeleteTask4 = HttpRequest.newBuilder().uri(tasksWithId4).DELETE().build();
        HttpResponse<String> responseDeleteTask4 = client.send(requestDeleteTask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteTask4.statusCode());
        assertEquals(4, taskManager.getListOfTasks().size());
        HttpResponse<String> responseDeleteTask4Twice = client.send(requestDeleteTask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseDeleteTask4Twice.statusCode());

        HttpRequest requestDeleteAllTasks = HttpRequest.newBuilder().uri(tasksUri).DELETE().build();
        HttpResponse<String> responseDeleteAllTasks = client.send(requestDeleteAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteAllTasks.statusCode());
        assertEquals(0, taskManager.getListOfTasks().size());
    }

    @Test
    public void testGetTaskAllAndById() throws IOException, InterruptedException {
        URI tasksUri = URI.create("http://localhost:8080/tasks");
        URI tasksWithId4 = URI.create("http://localhost:8080/tasks/4");
        URI tasksWithId6 = URI.create("http://localhost:8080/tasks/6");

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

        String jsonTask4 = gson.toJson(new Task(4,"Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 14:00", 180));
        String jsonAllTasks = gson.toJson(taskManager.getListOfTasks());

        HttpRequest requestGetTask4 = HttpRequest.newBuilder().uri(tasksWithId4).GET().build();
        HttpResponse<String> responseGetTask4 = client.send(requestGetTask4, HttpResponse.BodyHandlers.ofString());
        assertEquals(jsonTask4, responseGetTask4.body());
        assertEquals(200, responseGetTask4.statusCode());

        HttpRequest requestGetTask6 = HttpRequest.newBuilder().uri(tasksWithId6).GET().build();
        HttpResponse<String> responseGetTask6 = client.send(requestGetTask6, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetTask6.statusCode());

        HttpRequest requestGetAllTasks = HttpRequest.newBuilder().uri(tasksUri).GET().build();
        HttpResponse<String> responseGetAllTasks = client.send(requestGetAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetAllTasks.statusCode());
        assertEquals(jsonAllTasks, responseGetAllTasks.body());
    }

    @Test
    public void testWrongEndpoint() throws IOException, InterruptedException {
        URI tasksUri1 = URI.create("http://localhost:8080/tasks");
        URI tasksUri2 = URI.create("http://localhost:8080/tasks/34per9");
        URI tasksUri3 = URI.create("http://localhost:8080/tasks/five");

        String json = gson.toJson(new Task("Test addNewTask",
                "Test addNewTask description", TaskStatus.NEW, "08.02.25 14:00", 180));
        HttpRequest requestPut = HttpRequest.newBuilder().uri(tasksUri1).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> responsePut = client.send(requestPut, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responsePut.statusCode());

        HttpRequest requestGet34per9 = HttpRequest.newBuilder().uri(tasksUri2).GET().build();
        HttpResponse<String> responseGet34per9 = client.send(requestGet34per9, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGet34per9.statusCode());

        HttpRequest requestGetFiveTask = HttpRequest.newBuilder().uri(tasksUri3).GET().build();
        HttpResponse<String> responseGetFiveTask = client.send(requestGetFiveTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGetFiveTask.statusCode());
    }
}
