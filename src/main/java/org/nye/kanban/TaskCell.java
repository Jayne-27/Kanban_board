package org.nye.kanban;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

/**
 * VIEW: draws one Task as a "card" inside a ListView.
 *
 * A ListView normally shows toString() as plain text. With a custom cell factory
 * (listView.setCellFactory(v -> new TaskCell())) we decide what each row looks like.
 *
 * Note: ListView reuses cells while scrolling, so the nodes are built ONCE in the
 * constructor and updateItem() only changes their contents.
 */
public class TaskCell extends ListCell<Task> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final PseudoClass OVERDUE = PseudoClass.getPseudoClass("overdue");

    private final Label titleLabel = new Label();
    private final Label descriptionLabel = new Label();
    private final Label priorityLabel = new Label();
    private final Label dueDateLabel = new Label();
    private final VBox card;

    public TaskCell() {
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("card-description");
        descriptionLabel.setWrapText(true);
        priorityLabel.getStyleClass().add("priority-badge");
        dueDateLabel.getStyleClass().add("card-date");

        Region spacer = new Region();
        // javafx.scene.layout.Priority (grow behaviour) is not our task Priority, so write the full name
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox footer = new HBox(8, priorityLabel, spacer, dueDateLabel);
        footer.setAlignment(Pos.CENTER_LEFT);

        card = new VBox(6, titleLabel, descriptionLabel, footer);
        card.getStyleClass().add("card");

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setPrefWidth(0); // the cell takes the ListView's width, so long text wraps instead of scrolling sideways
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);

        if (empty || task == null) {
            setGraphic(null);
            return;
        }

        titleLabel.setText(task.getTitle());

        String description = task.getDescription();
        boolean hasDescription = description != null && !description.isBlank();
        descriptionLabel.setText(description);
        descriptionLabel.setVisible(hasDescription);
        descriptionLabel.setManaged(hasDescription); // managed=false means it takes no space when hidden

        // Swap the priority colour class (priority-low / -medium / -high)
        priorityLabel.setText(task.getPriority().toString());
        for (Priority p : Priority.values()) {
            priorityLabel.getStyleClass().remove(p.cssClass());
        }
        priorityLabel.getStyleClass().add(task.getPriority().cssClass());

        if (task.getDueDate() == null) {
            dueDateLabel.setText("");
        } else {
            dueDateLabel.setText("Due " + task.getDueDate().format(DATE_FORMAT));
        }
        dueDateLabel.pseudoClassStateChanged(OVERDUE, task.isOverdue());

        setGraphic(card);
    }
}
