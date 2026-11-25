package org.nye.kanban;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;

import java.util.Map;
import java.util.Optional;

/**
 * CONTROLLER: connects the model (Board/Task) and the view (kanban.fxml).
 *
 *  - @FXML fields are injected by FXMLLoader using the fx:id values in the FXML.
 *  - @FXML methods are called by onAction="#methodName" in the FXML.
 *  - initialize() runs automatically after all @FXML fields are injected.
 */
public class KanbanController {

    // ----- injected from kanban.fxml -----
    @FXML private ListView<Task> todoList;
    @FXML private ListView<Task> inProgressList;
    @FXML private ListView<Task> doneList;

    @FXML private Label todoCount;
    @FXML private Label inProgressCount;
    @FXML private Label doneCount;

    @FXML private Label statusLabel;
    @FXML private Label progressLabel;
    @FXML private ProgressBar progressBar;

    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button moveLeftButton;
    @FXML private Button moveRightButton;
    @FXML private MenuItem editMenuItem;
    @FXML private MenuItem deleteMenuItem;
    @FXML private MenuItem moveLeftMenuItem;
    @FXML private MenuItem moveRightMenuItem;

    // ----- model -----
    private final Board board = new Board();

    // ----- current selection -----
    private Task selectedTask;
    private Status selectedColumn;

    private Map<Status, ListView<Task>> listViews;

    @FXML
    private void initialize() {
        listViews = Map.of(
                Status.TODO, todoList,
                Status.IN_PROGRESS, inProgressList,
                Status.DONE, doneList);

        for (Status status : Status.values()) {
            setUpColumn(status, listViews.get(status));
        }

        setUpCounters();
        updateButtonStates();

        board.loadSampleData();
    }

    // =====================================================================
    //  Setup
    // =====================================================================

    private void setUpColumn(Status status, ListView<Task> listView) {
        listView.setItems(board.tasks(status));          // the view shows the model's list directly
        listView.setCellFactory(lv -> new TaskCell());   // draw each task as a card
        listView.setPlaceholder(new Label("No tasks"));

        // Only one task on the whole board is selected at a time.
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTask, newTask) -> onSelectionChanged(status, newTask));

        // Double-click a card to edit it
        listView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
                    && listView.getSelectionModel().getSelectedItem() != null) {
                onEditTask();
            }
        });
    }

    /** Counters and the progress bar update by themselves through bindings. */
    private void setUpCounters() {
        ObservableList<Task> todo = board.tasks(Status.TODO);
        ObservableList<Task> inProgress = board.tasks(Status.IN_PROGRESS);
        ObservableList<Task> done = board.tasks(Status.DONE);

        todoCount.textProperty().bind(Bindings.size(todo).asString());
        inProgressCount.textProperty().bind(Bindings.size(inProgress).asString());
        doneCount.textProperty().bind(Bindings.size(done).asString());

        IntegerBinding doneSize = Bindings.size(done);
        IntegerBinding totalSize = Bindings.createIntegerBinding(
                () -> todo.size() + inProgress.size() + done.size(),
                todo, inProgress, done);

        progressBar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> totalSize.get() == 0 ? 0.0 : (double) doneSize.get() / totalSize.get(),
                doneSize, totalSize));

        progressLabel.textProperty().bind(Bindings.createStringBinding(
                () -> doneSize.get() + " / " + totalSize.get() + " done",
                doneSize, totalSize));
    }

    /** Keeps toolbar buttons and menu items in sync with the current selection. */
    private void updateButtonStates() {
        boolean hasSelection = selectedTask != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
        editMenuItem.setDisable(!hasSelection);
        deleteMenuItem.setDisable(!hasSelection);

        moveLeftButton.setDisable(!hasSelection || selectedColumn == Status.TODO);
        moveLeftMenuItem.setDisable(!hasSelection || selectedColumn == Status.TODO);
        moveRightButton.setDisable(!hasSelection || selectedColumn == Status.DONE);
        moveRightMenuItem.setDisable(!hasSelection || selectedColumn == Status.DONE);
    }

    private void onSelectionChanged(Status column, Task task) {
        if (task != null) {
            selectedColumn = column;
            selectedTask = task;
            // clear the selection in the other two columns
            for (Status other : Status.values()) {
                if (other != column) {
                    listViews.get(other).getSelectionModel().clearSelection();
                }
            }
        } else if (column == selectedColumn) {
            // the selected column lost its selection, e.g. its task was deleted
            selectedTask = null;
            selectedColumn = null;
        }
        updateButtonStates();
    }

    // =====================================================================
    //  Actions (called from the FXML via onAction="#...")
    // =====================================================================

    @FXML
    private void onNewTask() {
        Optional<Task> result = TaskDialog.create(window());
        result.ifPresent(task -> {
            board.add(Status.TODO, task);
            select(Status.TODO, task);
            setStatus("Added \"" + task.getTitle() + "\"");
        });
    }

    @FXML
    private void onEditTask() {
        Task task = selectedTask;
        if (task == null) {
            return;
        }
        if (TaskDialog.edit(window(), task)) {
            listViews.get(selectedColumn).refresh(); // redraw the card with the new values
            setStatus("Updated \"" + task.getTitle() + "\"");
        }
    }

    @FXML
    private void onDeleteTask() {
        Task task = selectedTask;
        Status column = selectedColumn;
        if (task == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + task.getTitle() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.initOwner(window());
        confirm.setTitle("Delete Task");
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            board.remove(column, task);
            setStatus("Deleted \"" + task.getTitle() + "\"");
        }
    }

    @FXML
    private void onMoveLeft() {
        moveSelected(-1);
    }

    @FXML
    private void onMoveRight() {
        moveSelected(+1);
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION,
                "A simple Kanban board built with JavaFX.\n\n"
                        + "Double-click a card to edit it.\n"
                        + "Ctrl+N: new task, Ctrl+Left/Right: move, Delete: delete.");
        about.initOwner(window());
        about.setTitle("About");
        about.setHeaderText("Kanban Board");
        about.showAndWait();
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    /** direction = -1 for left, +1 for right. */
    private void moveSelected(int direction) {
        Task task = selectedTask;
        Status from = selectedColumn;
        if (task == null) {
            return;
        }
        int targetIndex = from.ordinal() + direction;
        if (targetIndex < 0 || targetIndex >= Status.values().length) {
            return;
        }
        Status to = Status.values()[targetIndex];

        board.move(task, from, to);
        select(to, task);
        setStatus("Moved \"" + task.getTitle() + "\" to " + to);
    }

    private void select(Status column, Task task) {
        ListView<Task> listView = listViews.get(column);
        listView.getSelectionModel().select(task);
        listView.scrollTo(task);
        listView.requestFocus();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private Window window() {
        return todoList.getScene().getWindow();
    }
}
