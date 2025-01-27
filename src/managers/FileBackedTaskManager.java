package managers;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.ManagerSaveException;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.read();
        return fileBackedTaskManager;
    }

    private static Optional<AbstractTask> fromString(String value) {
        String[] taskData = value.split(",");
        TaskType taskType = TaskType.valueOf(taskData[1]);

        return switch (taskType) {
            case TASK -> Optional.of(new Task(Integer.parseInt(taskData[0]), taskData[2],
                    taskData[4], TaskStatus.valueOf(taskData[3])));
            case EPIC -> Optional.of(new Epic(Integer.parseInt(taskData[0]), taskData[2],
                    taskData[4], TaskStatus.valueOf(taskData[3])));
            case SUBTASK -> Optional.of(new Subtask(Integer.parseInt(taskData[0]), taskData[2],
                    taskData[4], TaskStatus.valueOf(taskData[3]), Integer.parseInt(taskData[5])));
            default -> Optional.empty();
        };
    }

    private void read() {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                line  = br.readLine();
                if (fromString(line).isPresent()) {
                    AbstractTask task = fromString(line).get();
                    switch (task.getType()) {
                        case TASK:
                            storage.getTasksMap().put(task.getId(), (Task) task);
                            break;
                        case EPIC:
                            storage.getEpicsMap().put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            storage.getSubtasksMap().put(task.getId(), (Subtask) task);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw  new ManagerSaveException("Ошибка чтения файла");
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epicID\n");
            for (Task task : super.getListOfTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : super.getListOfEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Subtask subtask : super.getListOfSubtasks()) {
                fileWriter.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw  new ManagerSaveException("Ошибка записи файла");
        }
    }

    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return  updatedEpic;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
}