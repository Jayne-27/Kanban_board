# Kanban defense notes

## Defense-day requirements

- December defenses are in Room 181. Each defense lasts 20 minutes.
- Be present at the start of the entire chosen timeslot and stay for its full duration.
  Being late or leaving early can void the defense.
- Prepare a five-minute technical introduction. No slides are required.
- For an excellent (5) grade, expect to make a small change in the IDE without other tools.
- Grades are not disclosed during the defense. Further timeslots will open in January.

## Five-minute introduction

> My project is a small Kanban board built with JavaFX. It lets a user create tasks,
> edit them, delete them, and move them through To Do, In Progress, and Done. I
> organized it using a simple Model-View-Controller structure so that the task data,
> the screen layout, and the user actions are not all mixed into one class.
>
> The model is mainly `Task` and `Board`. A `Task` holds a title, description,
> priority, and optional due date. The text and object fields are JavaFX properties,
> so the interface can observe changes to them. `Priority` and `Status` are enums:
> they restrict values to the choices the program supports. `Board` stores an
> observable task list for each status. When a list changes, its `ListView` can
> update automatically.
>
> The view is declared in `kanban.fxml`. It defines the three columns, buttons,
> menus, and status area. `KanbanApp` loads that FXML file and the stylesheet.
> `KanbanController` is connected to the view by `fx:controller`; `@FXML` connects
> its fields and action methods to the FXML IDs and handlers.
>
> The controller connects user actions to the model. For example, creating a task
> opens `TaskDialog`, and a successful result is added to the To Do list. Moving a
> task removes it from one observable list and adds it to the next. The counters
> and completion progress use JavaFX bindings, so they recalculate when list sizes
> change. The controller also tracks the selected task and disables actions that
> are not valid for that selection.
>
> `TaskCell` is a custom `ListCell<Task>` that draws each task as a card. JavaFX
> reuses list cells while scrolling, so the cell creates its labels once and updates
> their contents in `updateItem`. `TaskDialog` is a separate form for creating or
> editing tasks; it validates the title and due date before allowing a save.
>
> The board currently uses sample data in memory. It does not save tasks when the
> application closes. I kept persistence out of scope so the implementation could
> focus on JavaFX models, observable collections, FXML, event handlers, and custom
> cells.

## Class and variable map

| Class or variable | What it represents |
|---|---|
| `KanbanApp` | Loads the FXML and CSS, creates the scene, and shows the window. |
| `KanbanController` | Handles UI actions and connects the view to the board. |
| `Board.columns` | Maps each `Status` to its `ObservableList<Task>`. |
| `Task` | One task; JavaFX properties hold values the UI may observe. |
| `Status` | The three ordered board columns. Their order is used when moving tasks. |
| `Priority` | The allowed priority values and their CSS class names. |
| `TaskCell` | Converts a task into a card inside a `ListView`. |
| `TaskDialog` | Creates or edits a task and checks its input. |
| `selectedTask` | The task currently selected by the user. |
| `selectedColumn` | The status column containing the selected task. |
| `listViews` | Lets the controller find the `ListView` for a given status. |
| `todo`, `inProgress`, `done` | The observable lists used to calculate counts and progress. |

## Likely questions

- **Why use MVC?** It separates data (`Task`, `Board`), layout (`kanban.fxml`), and
  UI behavior (`KanbanController`), making each part easier to follow or change.
- **What is an `ObservableList`?** A list that notifies JavaFX when items are added
  or removed. Each `ListView` displays one of these lists.
- **What does a JavaFX property do?** It stores a value that JavaFX can observe or
  bind to. For example, `titleProperty()` exposes a task title as a `StringProperty`.
- **What is a binding?** A value that JavaFX recalculates when its dependencies
  change. Here bindings update the column counts and completion progress.
- **Why use enums?** `Status` and `Priority` prevent arbitrary strings and make the
  allowed choices explicit.
- **Why override `updateItem`?** A `ListView` reuses cells. `updateItem` fills a
  reused cell with its current task and clears it when the row is empty.
- **What happens when a task moves?** `Board.move` removes it from the old list and
  adds it to the new list; both views observe their lists.
- **Does the board save data?** No. It currently stores tasks in memory and loads
  sample tasks at startup.
- **Why does the app use `Platform.startup`?** It starts JavaFX before creating a
  `Stage`, matching the startup pattern used in several project exercises.

## Small changes to rehearse

Practice making each change in the IDE, then explain which class owns it and why:

1. Change a sample task title or priority in `Board.loadSampleData`.
2. Add another priority value in `Priority` and explain where the enum is used.
3. Change the task title length limit in `TaskDialog.MAX_TITLE_LENGTH`.
4. Change the progress text or calculation in `KanbanController.setUpCounters`.
5. Add a simple validation rule in `TaskDialog.validate`.
6. Change the card text or date format in `TaskCell.updateItem`.

For a question about a different lesson example, identify its input, the event or
binding that handles it, and the output it changes. Do not assume every requested
change must affect the Kanban app.
