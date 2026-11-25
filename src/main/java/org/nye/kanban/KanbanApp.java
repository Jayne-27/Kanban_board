package org.nye.kanban;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Entry point: loads kanban.fxml, applies kanban.css and shows the window.
 * Run this class's main() to start the app.
 *
 * Project structure (Model-View-Controller):
 *   Model:      Task, Board, Priority, Status
 *   View:       kanban.fxml, kanban.css, TaskCell, TaskDialog
 *   Controller: KanbanController
 */
public class KanbanApp {

    static final String STYLESHEET = Objects.requireNonNull(
            KanbanApp.class.getResource("kanban.css"), "kanban.css not found").toExternalForm();

    public static void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(KanbanApp.class.getResource("kanban.fxml"));
        Parent root = loader.load(); // creates the nodes AND the KanbanController, then calls initialize()

        Scene scene = new Scene(root);
        scene.getStylesheets().add(STYLESHEET);

        stage.setTitle("Kanban Board");
        stage.setScene(scene);
        stage.setMinWidth(760);
        stage.setMinHeight(480);
        stage.show();
    }

    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                start(new Stage());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
