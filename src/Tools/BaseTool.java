package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasMouseInfoHolder;

import java.awt.*;

public abstract class BaseTool {
    protected Canvas canvas;
    protected ChoicesHolder choicesHolder;
    protected CanvasMouseInfoHolder mouseInfoHolder;
    protected CanvasCursorManager cursorManager;

    public BaseTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager) {
        this.canvas = canvas;
        this.choicesHolder = choicesHolder;
        this.mouseInfoHolder = mouseInfoHolder;
        this.cursorManager = cursorManager;
    }

    public void mousePressed() {}
    public void mouseReleased() {}
    public void mouseDragged() {}
    public void mouseMoved() {}
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }
    public void reset() {}
}
