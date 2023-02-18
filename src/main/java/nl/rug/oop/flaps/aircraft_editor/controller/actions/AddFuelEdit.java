package nl.rug.oop.flaps.aircraft_editor.controller.actions;

import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;

import javax.swing.table.TableModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class AddFuelEdit extends AbstractUndoableEdit {

    private double amount;
    private EditorModel editorModel;

    public AddFuelEdit(double amount, EditorModel editorModel) {
        this.amount = amount;
        this.editorModel = editorModel;
    }

    @Override
    public void undo() throws CannotUndoException {
        editorModel.addFuelToTank(-1 * amount);
    }

    @Override
    public void redo() throws CannotRedoException {
        editorModel.addFuelToTank(amount);;
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
