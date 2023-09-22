package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Toolstrip.Tool;
import Utils.MouseClick;
import Utils.MouseInfoHolder;

import java.awt.*;


public class PencilTool extends BaseTool {
    private Mode mode = null;

    public PencilTool(Canvas canvas, ChoicesHolder choicesHolder, MouseInfoHolder mouseInfoHolder) {
        super(canvas, choicesHolder, mouseInfoHolder);
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
            Point mousePosition = mouseInfoHolder.getCurrentMousePosition();
            canvas.getMainImage().setRGB(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale(), color);
            canvas.repaint();
        }
    }

    @Override
    public void mouseDragged() {
        if (mode != null) {
            // if drawing, figures out previous mouse position and new mouse position, and then applies pixels in between
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

    private enum Mode {
        DRAW, ERASE
    }
}
