package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Toolstrip.Tool;
import Utils.MouseClick;
import Utils.MouseInfoHolder;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasCursor;
import java.awt.*;


public class PencilTool extends BaseTool {
    private Mode mode = null;

    public PencilTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);
    }

    @Override
    public void mousePressed() {
        if (mode == null) {
            int color = 0;
            if (mouseInfoHolder.isLeftMouseButtonPressed()) {
                color = choicesHolder.getPaintColorAsIntRGB();
                mode = Mode.DRAW;
            }
            else if (mouseInfoHolder.isRightMouseButtonPressed()) {
                color = choicesHolder.getEraseColorAsIntRGB();
                mode = Mode.ERASE;
            }
            Point mousePosition = mouseInfoHolder.getCurrentMousePositionInImage();
            canvas.getMainImage().setRGB(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale(), color);
            canvas.repaint();
        }
    }

    @Override
    public void mouseDragged() {
        if (mode != null) {
            int previousMouseX = mouseInfoHolder.getPreviousMousePositionInImageX();
            int previousMouseY = mouseInfoHolder.getPreviousMousePositionInImageY();
            int mousePositionX = mouseInfoHolder.getCurrentMousePositionInImageX();
            int mousePositionY = mouseInfoHolder.getCurrentMousePositionInImageY();

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
                    int color = 0;
                    if (mode == Mode.DRAW) {
                        color = choicesHolder.getPaintColorAsIntRGB();
                    } else if (mode == Mode.ERASE) {
                        color = choicesHolder.getEraseColorAsIntRGB();
                    }
                    canvas.getMainImage().setRGB(previousMouseX / choicesHolder.getScale(), previousMouseY / choicesHolder.getScale(), color);
                }
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased() {
        if (mode == Mode.DRAW && !mouseInfoHolder.isLeftMouseButtonPressed()) {
            mode = null;
        }
        else if (mode == Mode.ERASE && !mouseInfoHolder.isRightMouseButtonPressed()) {
            mode = null;
        }

    }

    @Override
    public Cursor getCursor() {
        return cursorManager.get(CanvasCursor.PENCIL);
    }

    private enum Mode {
        DRAW, ERASE
    }
}
