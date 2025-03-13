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

public class HttpTaskManagerEpicsTest {
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
    public void testAddAndUpdateEpic() throws IOException, InterruptedException {
        URI epicUri = URI.create("http://localhost:8080/epics");
        URI epicWithId1 = URI.create("http://localhost:8080/epics/1");
        URI epicWithId2 = URI.create("http://localhost:8080/epics/2");

        String jsonAddEpic = gson.toJson(new Epic("Test addNewEpic",
                "Test addNewEpic description"));
        String jsonUpdateEpic = gson.toJson(new Epic(1, "Test updateEpic",
                "Test updateEpic description"));
        String jsonUpdateEpic2 = gson.toJson(new Epic(2,"Test addNewEpic",
                "Test addNewEpic description"));

        HttpRequest requestAddEpic1 = HttpRequest.newBuilder().uri(epicUri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonAddEpic)).build();
        HttpResponse<String> responseAddEpic1 = client.send(requestAddEpic1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseAddEpic1.statusCode());

        HttpRequest requestUpdateEpic = HttpRequest.newBuilder().uri(epicWithId1)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateEpic)).build();
        HttpResponse<String> responseUpdateEpic = client.send(requestUpdateEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseUpdateEpic.statusCode());

        HttpRequest requestUpdateEpic2 = HttpRequest.newBuilder().uri(epicWithId2)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdateEpic2)).build();
        HttpResponse<String> responseUpdateEpic2 = client.send(requestUpdateEpic2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseUpdateEpic2.statusCode());
    }

    @Test
    public void testDeleteEpicsAllAndById() throws IOException, InterruptedException {
        URI epicUri = URI.create("http://localhost:8080/epics");
        URI epicWithId1 = URI.create("http://localhost:8080/epics/1");

        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, 1, "08.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, 5, "09.02.25 11:00", 180));

        assertEquals(5, taskManager.getListOfEpics().size());
        assertEquals(2, taskManager.getListOfSubtasks().size());

        HttpRequest requestDeleteEpic1 = HttpRequest.newBuilder().uri(epicWithId1).DELETE().build();
        HttpResponse<String> responseDeleteEpic1 = client.send(requestDeleteEpic1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteEpic1.statusCode());
        assertEquals(4, taskManager.getListOfEpics().size());
        assertEquals(1, taskManager.getListOfSubtasks().size());

        HttpResponse<String> responseDeleteEpic1Twice = client.send(requestDeleteEpic1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseDeleteEpic1Twice.statusCode());

        HttpRequest requestDeleteAllEpics = HttpRequest.newBuilder().uri(epicUri).DELETE().build();
        HttpResponse<String> responseDeleteAllEpics = client.send(requestDeleteAllEpics, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteAllEpics.statusCode());
        assertEquals(0, taskManager.getListOfEpics().size());
        assertEquals(0, taskManager.getListOfSubtasks().size());
    }

    @Test
    public void testGetTaskAllAndByIdWithSubtasks() throws IOException, InterruptedException {
        URI epicUri = URI.create("http://localhost:8080/epics");
        URI epicWithId1 = URI.create("http://localhost:8080/epics/1");
        URI epicWithId6 = URI.create("http://localhost:8080/epics/6");
        URI epicWithId1Sub = URI.create("http://localhost:8080/epics/1/subtasks");
        URI epicWithId6Sub = URI.create("http://localhost:8080/epics/6/subtasks");

        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addEpic(new Epic("Test addNewEpic", "Test addNewEpic description"));
        taskManager.addSubtask(new Subtask("Test addNewSubtask1",
                "Test addNewSubtask1 description", TaskStatus.IN_PROGRESS, 1, "08.02.25 11:00", 180));
        taskManager.addSubtask(new Subtask("Test addNewSubtask2",
                "Test addNewSubtask2 description", TaskStatus.NEW, 5, "09.02.25 11:00", 180));

        String jsonEpic1 = gson.toJson(taskManager.getEpic(1));
        String jsonEpic1Sub = gson.toJson(taskManager.getSubtasksByEpicID(1));
        String jsonAllEpics = gson.toJson(taskManager.getListOfEpics());

        HttpRequest requestGetEpic1 = HttpRequest.newBuilder().uri(epicWithId1).GET().build();
        HttpResponse<String> responseGetEpic1 = client.send(requestGetEpic1, HttpResponse.BodyHandlers.ofString());
        assertEquals(jsonEpic1, responseGetEpic1.body());
        assertEquals(200, responseGetEpic1.statusCode());

        HttpRequest requestGetEpic1Sub = HttpRequest.newBuilder().uri(epicWithId1Sub).GET().build();
        HttpResponse<String> responseGetEpic1Sub = client.send(requestGetEpic1Sub, HttpResponse.BodyHandlers.ofString());
        assertEquals(jsonEpic1Sub, responseGetEpic1Sub.body());
        assertEquals(200, responseGetEpic1Sub.statusCode());

        HttpRequest requestGetEpic6 = HttpRequest.newBuilder().uri(epicWithId6).GET().build();
        HttpResponse<String> responseGetEpic6 = client.send(requestGetEpic6, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetEpic6.statusCode());

        HttpRequest requestGetEpic6Sub = HttpRequest.newBuilder().uri(epicWithId6Sub).GET().build();
        HttpResponse<String> responseGetEpic6Sub = client.send(requestGetEpic6Sub, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetEpic6Sub.statusCode());

        HttpRequest requestGetAllEpics = HttpRequest.newBuilder().uri(epicUri).GET().build();
        HttpResponse<String> responseGetAllEpics = client.send(requestGetAllEpics, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetAllEpics.statusCode());
        assertEquals(jsonAllEpics, responseGetAllEpics.body());
    }

    @Test
    public void testWrongEndpoint() throws IOException, InterruptedException {
        URI epicsUri1 = URI.create("http://localhost:8080/epics");
        URI epicsUri2 = URI.create("http://localhost:8080/epics/34per9");
        URI epicsUri3 = URI.create("http://localhost:8080/epics/five");

        String json = gson.toJson(new Epic("Test addNewEpic", "Test addNewEpic description"));
        HttpRequest requestPut = HttpRequest.newBuilder().uri(epicsUri1).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> responsePut = client.send(requestPut, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responsePut.statusCode());

        HttpRequest requestGet34per9 = HttpRequest.newBuilder().uri(epicsUri2).GET().build();
        HttpResponse<String> responseGet34per9 = client.send(requestGet34per9, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGet34per9.statusCode());

        HttpRequest requestGetFiveEpic = HttpRequest.newBuilder().uri(epicsUri3).GET().build();
        HttpResponse<String> responseGetFiveEpic = client.send(requestGetFiveEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, responseGetFiveEpic.statusCode());
    }
}
