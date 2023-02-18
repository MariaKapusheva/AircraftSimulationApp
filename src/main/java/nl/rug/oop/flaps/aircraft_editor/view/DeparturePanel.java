package nl.rug.oop.flaps.aircraft_editor.view;

import nl.rug.oop.flaps.aircraft_editor.controller.actions.DepartAction;
import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.aircraft_editor.model.EditorModelListener;

import javax.swing.*;

public class DeparturePanel extends JPanel implements EditorModelListener {

    private EditorModel editorModel;
    private EditorFrame editorFrame;

    private JTextField range;
    private JTextField estimatedProfit;
    private JTextField weight;
    private JPanel infoPanel;
    private JButton departureButton;

    public DeparturePanel(EditorModel editorModel, EditorFrame editorFrame) {
        this.editorModel = editorModel;
        this.editorFrame = editorFrame;

        editorModel.addListener(this);

        infoPanel = new JPanel();
        updateInfo();
        add(infoPanel);
    }

    private void updateInfo() {
        infoPanel.removeAll();
        infoPanel.revalidate();
        infoPanel.repaint();

        range = new JTextField("Range: " + editorModel.getRange());
        estimatedProfit = new JTextField("Estimated profit: " + editorModel.getProfitEstimation());
        weight = new JTextField("Total weight: " + editorModel.getTotalWeight());
        range.setEditable(false);
        estimatedProfit.setEditable(false);
        weight.setEditable(false);
        infoPanel.add(range);
        infoPanel.add(estimatedProfit);
        infoPanel.add(weight);

        departureButton = new JButton(new DepartAction(editorModel.getWorldSelectionModel(), editorModel.canDepart(), editorFrame));
        infoPanel.add(departureButton);
    }

    @Override
    public void selectedPointUpdate() {
        updateInfo();
    }
}
