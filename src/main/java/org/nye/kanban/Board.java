package org.nye.kanban;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 * MODEL: the whole board, meaning one ObservableList of tasks per column.
 *
 * ObservableList is the key idea: a ListView shows these lists directly, so adding or
 * removing a task here updates the screen automatically. The model does not know about the UI.
 */
public class Board {

    private final Map<Status, ObservableList<Task>> columns = new EnumMap<>(Status.class);

    public Board() {
        for (Status status : Status.values()) {
            columns.put(status, FXCollections.observableArrayList());
        }
    }

    public ObservableList<Task> tasks(Status status) {
        return columns.get(status);
    }

    public void add(Status status, Task task) {
        tasks(status).add(task);
    }

    public void remove(Status status, Task task) {
        tasks(status).remove(task);
    }

    /** Moves a task from one column to the end of another. */
    public void move(Task task, Status from, Status to) {
        if (tasks(from).remove(task)) {
            tasks(to).add(task);
        }
    }

    /** A few example cards so the board isn't empty on first start. */
    public void loadSampleData() {
        LocalDate today = LocalDate.now();
        add(Status.TODO, new Task("Write project report", "Describe architecture and features", Priority.HIGH, today.plusDays(7)));
        add(Status.TODO, new Task("Design app icon", "", Priority.LOW, null));
        add(Status.TODO, new Task("Prepare defense slides", "Demo: add, edit, move, delete tasks", Priority.MEDIUM, today.plusDays(10)));
        add(Status.IN_PROGRESS, new Task("Implement task dialog", "Form with validation", Priority.HIGH, today.plusDays(2)));
        add(Status.DONE, new Task("Set up Maven project", "JavaFX dependencies in pom.xml", Priority.MEDIUM, today.minusDays(3)));
    }
}
