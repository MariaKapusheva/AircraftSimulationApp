package nl.rug.oop.flaps.aircraft_editor.controller.actions;


import nl.rug.oop.flaps.aircraft_editor.model.EditorModel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SelectionController implements MouseListener {

    private static final int CLICK_RADIUS = 10;

    private final EditorModel editorModel;

    public SelectionController(EditorModel editorModel) {
        this.editorModel = editorModel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        editorModel.selectPoint(e.getPoint(), CLICK_RADIUS);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
