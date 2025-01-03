package managers;

import tasks.Task;
import utilities.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> viewedTasks;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        viewedTasks = new HashMap<>();
    }

    private void linkLast(Task task) {
        if (viewedTasks.containsKey(task.getId())) {
            removeNode(viewedTasks.get(task.getId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        viewedTasks.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private List<Task> getTasks() {
        List<Task> viewedTasksHistory = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            viewedTasksHistory.add(node.getItem());
            node = node.getNext();
        }
        return viewedTasksHistory;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> next = node.getNext();
        Node<Task> prev = node.getPrev();
        node.setItem(null);
        if (head != node && tail != node) {
            prev.setNext(next);
            next.setPrev(prev);
        } else if (head == node && tail != node) {
            next.setPrev(null);
            head = next;
        } else if (head != node && tail == node) {
            prev.setNext(null);
            tail = prev;
        } else {
            head = null;
            tail = null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (viewedTasks.get(id) != null && getHistory().contains(viewedTasks.get(id).getItem())) {
            removeNode(viewedTasks.get(id));
        }

    }
}
