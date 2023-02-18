package nl.rug.oop.flaps.aircraft_editor.view;

import nl.rug.oop.flaps.aircraft_editor.controller.actions.AddCargoAction;
import nl.rug.oop.flaps.aircraft_editor.controller.actions.AddFuelAction;
import nl.rug.oop.flaps.aircraft_editor.controller.actions.SelectionController;
import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.aircraft_editor.model.EditorModelListener;
import nl.rug.oop.flaps.simulation.model.cargo.CargoType;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Map;

public class EditorPanel extends JPanel implements EditorModelListener {

    private static final int POINT_SIZE = 10;

    private final EditorModel editorModel;
    private EditorFrame editorFrame;

    JPanel panelLeft;
    JPanel panelMiddle;

    private JPanel undoPanel;
    private UndoManager manager;
    private UndoableEditSupport undoSupport;

    public EditorPanel(EditorModel editorModel, EditorFrame editorFrame) {
        setPreferredSize(new Dimension(800, 400));
        manager = new UndoManager();
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(new UndoAdapter());

        this.editorModel = editorModel;
        this.editorFrame = editorFrame;
        editorModel.addListener(this);
        this.addMouseListener(new SelectionController(editorModel));

        panelLeft = new JPanel();
        panelMiddle = new JPanel();
        panelMiddle.setPreferredSize(new Dimension(260, 300));
        panelMiddle.setLayout(new GridLayout(0, 1));
        add(panelLeft);
        add(panelMiddle);

        undoPanel = new JPanel();
        undoPanel.setLayout(new GridLayout(0, 2));
        JButton undoButton = new JButton("undo");
        undoButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.undo();
            }
        });
        JButton redoButton = new JButton("redo");
        redoButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.redo();
            }
        });
        undoPanel.add(undoButton);
        undoPanel.add(redoButton);
    }

    private void drawFuelTanks(Graphics2D g2d) {
        Point2D selectedFuelTank = editorModel.getSelectedPoint();
        for(Point2D fuelPoint : editorModel.getFuelPoints()) {
            if(selectedFuelTank != null && selectedFuelTank.equals(fuelPoint)) {
                g2d.setColor(Color.GREEN);
                updateFuelTankInfo(editorModel);
            } else {
                g2d.setColor(Color.RED);
            }
            g2d.fillOval((int)fuelPoint.getX() - POINT_SIZE / 2, (int)fuelPoint.getY() - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
        }
    }

    private void drawCargoAreas(Graphics2D g2d) {
        Point2D selectedCargoArea = editorModel.getSelectedPoint();
        for(Point2D cargoPoint : editorModel.getCargoPoints()) {
            if(selectedCargoArea != null && selectedCargoArea.equals(cargoPoint)) {
                g2d.setColor(Color.CYAN);
                updateCargoAreaInfo(editorModel);
            } else {
                g2d.setColor(Color.BLUE);
            }
            g2d.fillOval((int)cargoPoint.getX() - POINT_SIZE / 2, (int)cargoPoint.getY() - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
        }
    }

    private void drawCOG(Graphics2D g2d) {
        Point2D filledCOG = editorModel.getCOG();
        g2d.setColor(Color.MAGENTA);
        g2d.fillRect((int)filledCOG.getX() - POINT_SIZE / 2, (int)filledCOG.getY() - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);

        Point2D emptyCOG = editorModel.getEmptyCog();
        g2d.setColor(Color.ORANGE);
        g2d.fillRect((int)emptyCOG.getX() - POINT_SIZE / 2, (int)emptyCOG.getY() - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
    }

    private void drawImage(Graphics g2d) {
        g2d.drawImage(editorModel.getImage(), 0, 0, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawImage(g2d);
        drawFuelTanks(g2d);
        drawCargoAreas(g2d);
        drawCOG(g2d);
    }

    @Override
    public void selectedPointUpdate() {
        repaint();
    }

    public void updateFuelTankInfo(EditorModel editorModel) {
        panelMiddle.removeAll();
        panelMiddle.revalidate();
        panelMiddle.repaint();

        if (editorModel.getSelectedFuelTank() != null) {
            panelMiddle.add(undoPanel);
            JTextField fuelTankName = new JTextField(editorModel.getFuelTankName());
            fuelTankName.setEditable(false);
            panelMiddle.add(fuelTankName);
            JTextField fuelTankCapacity = new JTextField("Capacity of this fuel tank: " + editorModel.getSelectedFuelTank().getCapacity() + "kg");
            fuelTankCapacity.setEditable(false);
            panelMiddle.add(fuelTankCapacity);
            JTextField fuelTankAmount = new JTextField("Current amount in this tank: " + editorModel.getFuelTankAmount() + "kg");
            fuelTankAmount.setEditable(false);
            panelMiddle.add(fuelTankAmount);

            JButton addFuelButton = new JButton(new AddFuelAction("Add", 1.0, editorModel, editorFrame, undoSupport));
            panelMiddle.add(addFuelButton);

            JButton removeFuelButton = new JButton(new AddFuelAction("Remove", -1.0, editorModel, editorFrame, undoSupport));
            panelMiddle.add(removeFuelButton);
        } else {
            panelMiddle.add(new JLabel("No information found "));
        }
    }

    public void updateCargoAreaInfo(EditorModel editorModel) {
        panelMiddle.removeAll();
        panelMiddle.revalidate();
        panelMiddle.repaint();

        if (editorModel.getSelectedCargoArea() != null) {
            panelMiddle.add(undoPanel);
            JTextField cargoAreaName = new JTextField(editorModel.getCargoAreaName());
            cargoAreaName.setEditable(false);
            panelMiddle.add(cargoAreaName);
            JTextField cargoAreaCapacity = new JTextField("Capacity of this cargo area: " + editorModel.getCargoAreaCapacity() + "kg");
            cargoAreaCapacity.setEditable(false);
            JTextField cargoAreaAmount = new JTextField("Current weight of this area: " + editorModel.getCargoAreaAmount() + "kg");
            cargoAreaAmount.setEditable(false);
            panelMiddle.add(cargoAreaAmount);
            panelMiddle.add(cargoAreaCapacity);

            Map<String, CargoType> cargoMap = editorModel.getCargoTypes();
            for (Map.Entry<String, CargoType> entry : cargoMap.entrySet()) {
                JButton cargoButton = new JButton(new AddCargoAction(entry.getKey(), entry.getValue(), editorModel, editorFrame, undoSupport));
                panelMiddle.add(cargoButton);
            }
        } else {
            panelMiddle.add(new JLabel("No information found"));
        }
    }

    public class UndoAdapter implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            UndoableEdit edit = e.getEdit();
            manager.addEdit(edit);
        }
    }
}
