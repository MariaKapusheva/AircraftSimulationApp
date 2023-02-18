package nl.rug.oop.flaps.aircraft_editor.view;

import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;
import nl.rug.oop.flaps.simulation.model.aircraft.Aircraft;
import nl.rug.oop.flaps.simulation.model.world.WorldSelectionModel;

import javax.swing.*;
import java.awt.*;

/**
 * The main frame in which the editor should be displayed.
 *
 * @author T.O.W.E.R.
 */
public class EditorFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public EditorFrame(Aircraft aircraft, WorldSelectionModel selectionModel) {
        super("Aircraft Editor");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // TODO: Add UI components to show the aircraft here. pack() should be called afterwards.
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        EditorModel editorModel = new EditorModel(aircraft);

        add(new EditorPanel(editorModel, this), BorderLayout.NORTH);
        add(new DeparturePanel(editorModel, this), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        /*
         * To get the destination airport:
         * selectionModel.getSelectedDestinationAirport()
         * To get the origin airport:
         * selectionModel.getSelectedAirport()
         * Other than this, the only place where you you need to use the selectionModel is passing it to the DepartAction
         */
    }
}
