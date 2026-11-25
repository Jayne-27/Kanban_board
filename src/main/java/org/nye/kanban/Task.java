package org.nye.kanban;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * MODEL: one card on the board.
 *
 * Fields are JavaFX properties (not plain Strings) so the UI can observe and bind to them,
 * the same idea as in PropertyBasicsDemo / TemperatureConverterUI.
 * Pattern for each property: private field + getter + setter + xxxProperty() accessor.
 */
public class Task {

    private final StringProperty title = new SimpleStringProperty(this, "title", "");
    private final StringProperty description = new SimpleStringProperty(this, "description", "");
    private final ObjectProperty<Priority> priority = new SimpleObjectProperty<>(this, "priority", Priority.MEDIUM);
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>(this, "dueDate");

    public Task() {
    }

    public Task(String title, String description, Priority priority, LocalDate dueDate) {
        setTitle(title);
        setDescription(description);
        setPriority(priority);
        setDueDate(dueDate);
    }

    /** A task is overdue when it has a due date that is before today. */
    public boolean isOverdue() {
        return getDueDate() != null && getDueDate().isBefore(LocalDate.now());
    }

    // --- title ---
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    // --- description ---
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    // --- priority ---
    public Priority getPriority() { return priority.get(); }
    public void setPriority(Priority value) { priority.set(value); }
    public ObjectProperty<Priority> priorityProperty() { return priority; }

    // --- due date (may be null = no deadline) ---
    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate value) { dueDate.set(value); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    @Override
    public String toString() {
        return getTitle();
    }
}
