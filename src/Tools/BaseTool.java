package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Utils.MouseInfoHolder;
import Canvas.CanvasMouseInfoHolder;

import java.awt.*;

public abstract class BaseTool {
    protected Canvas canvas;
    protected ChoicesHolder choicesHolder;
    protected CanvasMouseInfoHolder mouseInfoHolder;

    public BaseTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder) {
        this.canvas = canvas;
        this.choicesHolder = choicesHolder;
        this.mouseInfoHolder = mouseInfoHolder;
    }

    public void mousePressed() {}
    public void mouseReleased() {}
    public void mouseDragged() {}
    public void mouseMoved() {}
}
