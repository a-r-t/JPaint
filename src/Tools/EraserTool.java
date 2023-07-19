package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Utils.MouseClick;
import Utils.MouseInfoHolder;

import java.awt.*;


public class EraserTool extends BaseTool {
    private Mode mode = null;

    public EraserTool(Canvas canvas, ChoicesHolder choicesHolder, MouseInfoHolder mouseInfoHolder) {
        super(canvas, choicesHolder, mouseInfoHolder);
    }

    @Override
    public void mousePressed() {
        if (mode == null && mouseInfoHolder.isLeftMouseButtonPressed()) {
            int color = choicesHolder.getEraseColorAsIntRGB();
            int mousePositionX = mouseInfoHolder.getCurrentMousePositionX();
            int mousePositionY = mouseInfoHolder.getCurrentMousePositionY();
            canvas.getMainImage().setRGB(mousePositionX / choicesHolder.getScale(), mousePositionY / choicesHolder.getScale(), color);
            mode = mode.ERASE;
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

    private enum Mode {
        ERASE
    }
}
