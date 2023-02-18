package nl.rug.oop.flaps.aircraft_editor.controller.actions;

import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.aircraft_editor.view.EditorFrame;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.event.ActionEvent;

public class AddFuelAction extends AbstractAction {

    private EditorModel editorModel;
    private EditorFrame editorFrame;
    private Double scale;

    protected UndoableEditSupport undoSupport;

    public AddFuelAction(String name, Double scale, EditorModel editorModel, EditorFrame editorFrame, UndoableEditSupport undoSupport) {
        super(name);
        this.editorModel = editorModel;
        this.scale = scale;
        this.editorFrame = editorFrame;
        this.undoSupport = undoSupport;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean error = true;
        while(error){
            try{
                String input = JOptionPane.showInputDialog(editorFrame, "How much? (kg)");
                if(input == null) {
                    break;
                }
                double amount = Integer.parseInt(input) * scale;
                if(editorModel.getFuelTankCapacity() - editorModel.getFuelTankAmount() < amount) {
                    JOptionPane.showMessageDialog(editorFrame, "Cannot exceed maximum fuel capacity");
                }
                else if (editorModel.getFuelTankAmount() + amount < 0) {
                    JOptionPane.showMessageDialog(editorFrame, "Cannot remove this much fuel");
                }
                else if(editorModel.getSelectedFuelTank() != null) {
                    editorModel.addFuelToTank(amount);
                    UndoableEdit edit = new AddFuelEdit(amount, editorModel);
                    undoSupport.postEdit(edit);
                }
                error = false;
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(editorFrame, "Please input a number");
            }
        }
    }
}
