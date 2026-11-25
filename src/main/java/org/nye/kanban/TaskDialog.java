package org.nye.kanban;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A modal form for creating or editing a Task (same GridPane + validation idea as
 * ResponsiveGridFormUI and PasswordValidator).
 *
 * Validation uses bindings: the error text is a StringBinding computed from the fields, and
 * the Save button is disabled while that text is not empty. No manual "check" button needed.
 */
public final class TaskDialog {

    static final int MAX_TITLE_LENGTH = 60;

    private TaskDialog() {
    }

    /** Shows an empty form and returns the new task, or empty if the user cancelled. */
    public static Optional<Task> create(Window owner) {
        return show(owner, null);
    }

    /** Shows the form filled with the task's values. Returns true if the user saved the changes. */
    public static boolean edit(Window owner, Task task) {
        return show(owner, task).isPresent();
    }

    private static Optional<Task> show(Window owner, Task existing) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(existing == null ? "New Task" : "Edit Task");
        dialog.setHeaderText(existing == null ? "Create a new task" : "Edit \"" + existing.getTitle() + "\"");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(KanbanApp.STYLESHEET);

        // ----- form fields -----
        TextField titleField = new TextField();
        titleField.setPromptText("What needs to be done?");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Optional details");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        ComboBox<Priority> priorityBox = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("No due date");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        if (existing != null) {
            titleField.setText(existing.getTitle());
            descriptionArea.setText(existing.getDescription());
            priorityBox.setValue(existing.getPriority());
            dueDatePicker.setValue(existing.getDueDate());
        } else {
            priorityBox.setValue(Priority.MEDIUM);
        }

        // ----- layout -----
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        ColumnConstraints labelColumn = new ColumnConstraints();
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        fieldColumn.setPrefWidth(320);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        grid.addRow(0, new Label("Title:"), titleField);
        grid.addRow(1, new Label("Description:"), descriptionArea);
        grid.addRow(2, new Label("Priority:"), priorityBox);
        grid.addRow(3, new Label("Due date:"), dueDatePicker);
        grid.add(errorLabel, 1, 4);
        dialog.getDialogPane().setContent(grid);

        // ----- validation with bindings -----
        LocalDate originalDate = existing == null ? null : existing.getDueDate();
        StringBinding error = Bindings.createStringBinding(
                () -> validate(titleField.getText(), dueDatePicker.getValue(), originalDate),
                titleField.textProperty(), dueDatePicker.valueProperty());
        errorLabel.textProperty().bind(error);

        Node saveButton = dialog.getDialogPane().lookupButton(saveType);
        saveButton.disableProperty().bind(error.isNotEmpty());

        // ----- turn the clicked button into a result -----
        dialog.setResultConverter(button -> {
            if (button != saveType) {
                return null; // cancelled, so showAndWait() returns Optional.empty()
            }
            Task task = existing != null ? existing : new Task();
            task.setTitle(titleField.getText().trim());
            task.setDescription(descriptionArea.getText().trim());
            task.setPriority(priorityBox.getValue());
            task.setDueDate(dueDatePicker.getValue());
            return task;
        });

        Platform.runLater(titleField::requestFocus);
        return dialog.showAndWait();
    }

    /**
     * Returns an error message, or "" if the input is valid.
     * Kept separate from the UI so it is easy to explain (and to unit-test).
     *
     * @param originalDate the task's current due date when editing; an existing past date is allowed
     *                     to stay, so you can still edit an overdue task.
     */
    static String validate(String title, LocalDate dueDate, LocalDate originalDate) {
        String trimmed = title == null ? "" : title.trim();
        if (trimmed.isEmpty()) {
            return "Title is required.";
        }
        if (trimmed.length() > MAX_TITLE_LENGTH) {
            return "Title must be at most " + MAX_TITLE_LENGTH + " characters (now " + trimmed.length() + ").";
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now()) && !dueDate.equals(originalDate)) {
            return "Due date cannot be in the past.";
        }
        return "";
    }
}
