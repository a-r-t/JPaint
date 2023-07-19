package Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class MouseInfoHolder {
    private Point currentMousePosition;
    private Point previousMousePosition;
    private HashSet<MouseClick> pressedButtons;
    private HashSet<MouseClick> previousPressedButtons;

    public MouseInfoHolder() {
        pressedButtons = new HashSet<>();
        previousPressedButtons = new HashSet<>();
    }

    public void updateMousePosition(Point mousePosition) {
        previousMousePosition = currentMousePosition;
        currentMousePosition = mousePosition;
    }

    public void mouseButtonPressed(MouseClick mouseClick) {
        previousPressedButtons = new HashSet<>(pressedButtons);
        pressedButtons.add(mouseClick);
    }

    public void mouseButtonReleased(MouseClick mouseClick) {
        previousPressedButtons = new HashSet<>(pressedButtons);
        pressedButtons.remove(mouseClick);
    }

    public Point getCurrentMousePosition() {
        return currentMousePosition;
    }

    public int getCurrentMousePositionX() {
        return currentMousePosition.x;
    }

    public int getCurrentMousePositionY() {
        return currentMousePosition.y;
    }

    public Point getPreviousMousePosition() {
        return previousMousePosition;
    }

    public int getPreviousMousePositionX() {
        return previousMousePosition.x;
    }

    public int getPreviousMousePositionY() {
        return previousMousePosition.y;
    }


    public boolean isMouseButtonPressed(MouseClick mouseClick) {
        return pressedButtons.contains(mouseClick);
    }

    public boolean isMouseButtonNewlyPressed(MouseClick mouseClick) {
        return pressedButtons.contains(mouseClick) && !previousPressedButtons.contains(mouseClick);
    }

    public boolean isLeftMouseButtonPressed() {
        return isMouseButtonPressed(MouseClick.LEFT_CLICK);
    }

    public boolean isRightMouseButtonPressed() {
        return isMouseButtonPressed(MouseClick.RIGHT_CLICK);
    }

    public boolean isMiddleMouseButtonPressed() {
        return isMouseButtonPressed(MouseClick.MIDDLE_CLICK);
    }

    public boolean isLeftMouseButtonNewlyPressed() {
        return isMouseButtonNewlyPressed(MouseClick.LEFT_CLICK);
    }

    public boolean isRightMouseButtonNewlyPressed() {
        return isMouseButtonNewlyPressed(MouseClick.RIGHT_CLICK);
    }

    public boolean isMiddleMouseButtonNewlyPressed() {
        return isMouseButtonNewlyPressed(MouseClick.MIDDLE_CLICK);
    }
}
