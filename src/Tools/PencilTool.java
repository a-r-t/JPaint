package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasCursor;
import java.awt.*;
import java.awt.event.KeyEvent;


public class PencilTool extends BaseTool {
    private Mode mode = null;

    // below instance vars are used for straight line drawing (when shift is pressed)
    private boolean isShiftPressed;
    private Direction chosenDirection = null; // only used for straight line mode (when shift is held down)
    private int xLocationAnchor; // keeps track of where x location should be for vertical straight line drawing
    private int yLocationAnchor; // keeps track of where y location should be for horizontal straight line drawing

    public PencilTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);

        // detects if certain keys are being pressed to use with other events
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        isShiftPressed = true;
                    }
                }
                else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        isShiftPressed = false;
                        chosenDirection = null;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void mousePressed() {
        if (mode == null) {
            canvas.getCanvasHistory().createPerformedState();

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

            while (!isDrawingCompleted(previousMouseX, previousMouseY, mousePositionX, mousePositionY)) {
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

                // if shift is held down and no direction is chosen yet, set chosen direction to signify straight line mode is activated
                if (isShiftPressed && chosenDirection == null) {
                    if (xOffset != 0) {
                        chosenDirection = Direction.HORIZONTAL;
                        yLocationAnchor = previousMouseY;
                    }
                    else if (yOffset != 0) {
                        chosenDirection = Direction.VERTICAL;
                        xLocationAnchor = previousMouseX;
                    }
                }

                // if chosenDirection isn't null, it means straight line mode is activated, and only one direction (either x or y) should be updated
                // otherwise, both are updated like normal
                if (chosenDirection == null) {
                    previousMouseX += xOffset;
                    previousMouseY += yOffset;
                }
                else if (chosenDirection == Direction.HORIZONTAL) {
                    previousMouseX += xOffset;
                    previousMouseY = yLocationAnchor;
                }
                else if (chosenDirection == Direction.VERTICAL) {
                    previousMouseY += yOffset;
                    previousMouseX = xLocationAnchor;
                }

                if (previousMouseX >= 0 && previousMouseX / choicesHolder.getScale() < canvas.getMainImage().getWidth() && previousMouseY >= 0 && previousMouseY / choicesHolder.getScale() < canvas.getMainImage().getHeight()) {
                    int color = 0;
                    if (mode == Mode.DRAW) {
                        color = choicesHolder.getPaintColorAsIntRGB();
                    }
                    else if (mode == Mode.ERASE) {
                        color = choicesHolder.getEraseColorAsIntRGB();
                    }
                    canvas.getMainImage().setRGB(previousMouseX / choicesHolder.getScale(), previousMouseY / choicesHolder.getScale(), color);
                }
            }
            canvas.repaint();
        }
    }

    private boolean isDrawingCompleted(int previousMouseX, int previousMouseY, int mousePositionX, int mousePositionY) {
        if (chosenDirection == null && (previousMouseX != mousePositionX || previousMouseY != mousePositionY)) {
            return false;
        }
        else {
           if (chosenDirection == Direction.HORIZONTAL && previousMouseX != mousePositionX) {
               return false;
           }
           else if (chosenDirection == Direction.VERTICAL && previousMouseY != mousePositionY) {
               return false;
           }
        }
        return true;
    }

    @Override
    public void mouseReleased() {
        if (mode == Mode.DRAW && !mouseInfoHolder.isLeftMouseButtonPressed()) {
            mode = null;
        }
        else if (mode == Mode.ERASE && !mouseInfoHolder.isRightMouseButtonPressed()) {
            mode = null;
        }

        // reset straight line mode
        chosenDirection = null;
    }

    @Override
    public Cursor getCursor() {
        return cursorManager.get(CanvasCursor.PENCIL);
    }

    private enum Mode {
        DRAW, ERASE
    }

    private enum Direction {
        HORIZONTAL, VERTICAL
    }
}
