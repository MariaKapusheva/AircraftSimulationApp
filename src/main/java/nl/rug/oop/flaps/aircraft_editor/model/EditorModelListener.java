package nl.rug.oop.flaps.aircraft_editor.model;

public interface EditorModelListener {
    default void selectedPointUpdated() {}

    void selectedPointUpdate();
}
