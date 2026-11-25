package org.nye.kanban;

/** How urgent a task is. Each priority has its own CSS class so the card badge gets a colour. */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    /** CSS style class used by TaskCell, e.g. "priority-high" (see kanban.css). */
    public String cssClass() {
        return "priority-" + name().toLowerCase();
    }

    /** ComboBox and Label use toString(), so return the friendly name. */
    @Override
    public String toString() {
        return displayName;
    }
}
