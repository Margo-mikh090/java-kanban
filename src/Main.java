import managers.InMemoryTaskManager;
import statuses.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task findJob = new Task("Найти работу", "Найти вакансии, прийти на собеседование", TaskStatus.NEW);
        Task findJobAdded = taskManager.addTask(findJob);
        System.out.println(findJobAdded);

        Task newFindJob = new Task(findJob.getId(), "Найти работу", "Найти вакансии, прийти на собеседование", TaskStatus.DONE);
        Task newFindJobUpdated = taskManager.updateTask(newFindJob);
        System.out.println(newFindJobUpdated);

        Epic makeSoup = new Epic("Сварить суп", "Надо уметь варить суп!");
        Epic makeSoupAdded = taskManager.addEpic(makeSoup);
        System.out.println(makeSoupAdded);

        Subtask makeBouillon = new Subtask("Сварить бульон", "Рецепт: мясо, соль, лук, морковка, вода", TaskStatus.DONE, makeSoup.getId());
        Subtask makeBouillonAdded = taskManager.addSubtask(makeBouillon);
        System.out.println(makeBouillonAdded);

        Subtask peelThePotatoes = new Subtask("Почистить картошку", "И нарезать!", TaskStatus.NEW, makeSoup.getId());
        Subtask peelThePotatoesAdded = taskManager.addSubtask(peelThePotatoes);
        System.out.println(peelThePotatoesAdded);
        System.out.println(makeSoupAdded);

        Epic newMakeSoup = new Epic(makeSoup.getId(), "Сварить суп", "Я почти умею варить суп!", makeSoup.getSubtaskIDs());
        Epic newMakeSoupUpdated = taskManager.updateEpic(newMakeSoup);
        System.out.println(newMakeSoupUpdated);

        System.out.println(taskManager.getSubtask(peelThePotatoes.getId()));
        taskManager.removeSubtask(makeBouillon.getId());
        System.out.println(newMakeSoupUpdated);

        Epic doHomework = new Epic("Сделать домашку", "От Яндекс практикума");
        Epic doHomeworkAdded = taskManager.addEpic(doHomework);

        Subtask writeCode = new Subtask("Написать код программы", " ", TaskStatus.DONE, doHomework.getId());
        Subtask writeCodeAdded = taskManager.addSubtask(writeCode);
        System.out.println(writeCodeAdded);
        System.out.println(doHomeworkAdded);

        taskManager.removeAllSubtasks();
        System.out.println(doHomeworkAdded);
        System.out.println(newMakeSoupUpdated);



    }
}
