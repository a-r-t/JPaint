package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Utils.MouseClick;
import Utils.MouseInfoHolder;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasCursor;

import java.awt.*;


public class EraserTool extends BaseTool {
    private Mode mode = null;

    public EraserTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);
    }

    @Override
    public void mousePressed() {
        if (mode == null && mouseInfoHolder.isLeftMouseButtonPressed()) {
            canvas.getCanvasHistory().createPerformedState();

            int color = choicesHolder.getEraseColorAsIntRGB();
            Point mousePosition = mouseInfoHolder.getCurrentMousePositionInImage();
            canvas.getMainImage().setRGB(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale(), color);
            mode = Mode.ERASE;
            canvas.repaint();
        }
    }

    @Override
    public void mouseDragged() {
        if (mode != null) {
            int previousMouseX = mouseInfoHolder.getPreviousMousePositionX();
            int previousMouseY = mouseInfoHolder.getPreviousMousePositionY();
            int mousePositionX = mouseInfoHolder.getCurrentMousePositionX();
            int mousePositionY = mouseInfoHolder.getCurrentMousePositionY();

            while (previousMouseX != mousePositionX || previousMouseY != mousePositionY) {
                int xOffset = 0;
                int yOffset = 0;
                if (previousMouseX > mousePositionX) {
                    xOffset = -1;
                } else if (previousMouseX < mousePositionX) {
                    xOffset = 1;
                }
                if (previousMouseY > mousePositionY) {
                    yOffset = -1;
                } else if (previousMouseY < mousePositionY) {
                    yOffset = 1;
                }
                previousMouseX += xOffset;
                previousMouseY += yOffset;

                if (previousMouseX >= 0 && previousMouseX / choicesHolder.getScale() < canvas.getMainImage().getWidth() && previousMouseY >= 0 && previousMouseY / choicesHolder.getScale() < canvas.getMainImage().getHeight()) {
                    int color = choicesHolder.getEraseColorAsIntRGB();
                    canvas.getMainImage().setRGB(previousMouseX / choicesHolder.getScale(), previousMouseY / choicesHolder.getScale(), color);
                }
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased() {
        if (mode == Mode.ERASE && !mouseInfoHolder.isLeftMouseButtonPressed()) {
            mode = null;
        }
    }

    @Override
    public Cursor getCursor() {
        // TODO: Figure out eraser cursor
        return super.getCursor();
        // return cursorManager.get(CanvasCursor);
    }

    private enum Mode {
        ERASE
    }
}
