package org.nye.kanban;

/**
 * The three columns of the board, in left-to-right order.
 * The order matters: "Move Left/Right" uses ordinal() - 1 / + 1.
 */
public enum Status {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
