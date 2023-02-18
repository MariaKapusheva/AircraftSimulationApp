package nl.rug.oop.flaps.aircraft_editor.controller.actions;

import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.aircraft_editor.view.EditorFrame;
import nl.rug.oop.flaps.simulation.model.cargo.CargoType;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.event.ActionEvent;

public class AddCargoAction extends AbstractAction {

    private EditorModel editorModel;
    private EditorFrame editorFrame;
    private CargoType cargoType;

    protected UndoableEditSupport undoSupport;

    public AddCargoAction(String name, CargoType cargoType, EditorModel editorModel, EditorFrame editorFrame, UndoableEditSupport undoSupport) {
        super(name);
        this.editorModel = editorModel;
        this.editorFrame = editorFrame;
        this.cargoType = cargoType;
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
                int weight = Integer.parseInt(input);
                if(editorModel.getCargoAreaCapacity() - editorModel.getCargoAreaAmount() < weight) {
                    JOptionPane.showMessageDialog(editorFrame, "Cannot exceed maximum cargo capacity");
                }
                else if(editorModel.getSelectedCargoArea() != null) {
                    editorModel.addCargo(cargoType, weight);
                    UndoableEdit edit = new AddCargoEdit(editorModel, cargoType, weight);
                    undoSupport.postEdit(edit);
                }
                error = false;
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(editorFrame, "Please input a number");
            }
        }
    }
}
