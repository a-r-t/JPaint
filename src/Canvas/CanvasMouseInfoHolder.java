package Canvas;

import Utils.MouseInfoHolder;

import java.awt.*;

public class CanvasMouseInfoHolder extends MouseInfoHolder {
    private Point currentMousePositionInImage;
    private Point previousMousePositionInImage;
    private Canvas canvas;

    public CanvasMouseInfoHolder(Canvas canvas) {
        super();
        this.canvas = canvas;
    }

    @Override
    public void updateMousePosition(Point mousePosition) {
        super.updateMousePosition(mousePosition);
        previousMousePositionInImage = currentMousePositionInImage;
        currentMousePositionInImage = new Point(mousePosition.x - canvas.getStartX(), mousePosition.y - canvas.getStartY());
    }

    public Point getCurrentMousePositionInImage() {
        return currentMousePositionInImage;
    }

    public int getCurrentMousePositionInImageX() {
        return currentMousePositionInImage.x;
    }

    public int getCurrentMousePositionInImageY() {
        return currentMousePositionInImage.y;
    }

    public Point getPreviousMousePositionInImage() {
        return previousMousePositionInImage;
    }

    public int getPreviousMousePositionInImageX() {
        return previousMousePositionInImage.x;
    }

    public int getPreviousMousePositionInImageY() {
        return previousMousePositionInImage.y;
    }
}
