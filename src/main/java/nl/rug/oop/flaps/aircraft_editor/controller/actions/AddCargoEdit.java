package nl.rug.oop.flaps.aircraft_editor.controller.actions;

import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.simulation.model.cargo.CargoType;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class AddCargoEdit extends AbstractUndoableEdit {

    private EditorModel editorModel;
    private CargoType cargoType;
    private int weight;

    public AddCargoEdit(EditorModel editorModel, CargoType cargoType, int weight) {
        this.editorModel = editorModel;
        this.cargoType = cargoType;
        this.weight = weight;
    }

    @Override
    public void undo() throws CannotUndoException {
        editorModel.removeCargo(cargoType, weight);
    }

    @Override
    public void redo() throws CannotRedoException {
        editorModel.addCargo(cargoType, weight);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

}
