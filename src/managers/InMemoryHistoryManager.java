package managers;

import tasks.AbstractTask;
import utilities.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<AbstractTask>> viewedTasks;
    private Node<AbstractTask> head;
    private Node<AbstractTask> tail;

    public InMemoryHistoryManager() {
        viewedTasks = new HashMap<>();
    }

    private void linkLast(AbstractTask task) {
        if (viewedTasks.containsKey(task.getId())) {
            removeNode(viewedTasks.get(task.getId()));
        }
        final Node<AbstractTask> oldTail = tail;
        final Node<AbstractTask> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        viewedTasks.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private List<AbstractTask> getTasks() {
        List<AbstractTask> viewedTasksHistory = new ArrayList<>();
        Node<AbstractTask> node = head;
        while (node != null) {
            viewedTasksHistory.add(node.getItem());
            node = node.getNext();
        }
        return viewedTasksHistory;
    }

    private void removeNode(Node<AbstractTask> node) {
        Node<AbstractTask> next = node.getNext();
        Node<AbstractTask> prev = node.getPrev();
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
    public List<AbstractTask> getHistory() {
        return getTasks();
    }

    @Override
    public void add(AbstractTask task) {
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
