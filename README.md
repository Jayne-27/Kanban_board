# Kanban Board

A JavaFX desktop Kanban board with To Do, In Progress, and Done columns. You can create, edit, delete, and move tasks. The application uses sample tasks stored in memory; tasks are not saved when it closes.

## Requirements

- OpenJDK 27
- Maven 3.9 or later
- Internet access for Maven to download dependencies on the first build
- A desktop environment supported by JavaFX

This application uses JavaFX 27.0.1 and does not rely on external APIs, services, or datasets.

## Build and run

From this project directory, run:

```text
mvn clean compile
mvn javafx:run
```

In IntelliJ IDEA, open this folder as its own project, import it as a Maven project, and select OpenJDK 27 as the Project SDK and Maven JDK. The application entry point is `org.nye.kanban.KanbanApp`.

## Structure

- `Task`, `Board`, `Status`, `Priority`: task and board model.
- `KanbanController`: connects UI actions to the model.
- `TaskCell`, `TaskDialog`: task card and create/edit form.
- `src/main/resources/org/nye/kanban/kanban.fxml`: JavaFX layout.
- `src/main/resources/org/nye/kanban/kanban.css`: styling.
- `KANBAN_DEFENSE.md`: defense requirements, introduction, and study notes.

## Submission

Include this README, `pom.xml`, and `src` in one ZIP named with your Neptun code (for example `ABC123.zip`). Ensure the archive is below 50 MB. Do not include generated `target` output, IDE settings, or Git data.
