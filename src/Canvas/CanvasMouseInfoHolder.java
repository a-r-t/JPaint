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
        this.currentMousePositionInImage = new Point(0, 0);
        this.previousMousePositionInImage = new Point(0, 0);
    }

    @Override
    public void updateMousePosition(Point mousePosition) {
        super.updateMousePosition(mousePosition);
        previousMousePositionInImage = currentMousePositionInImage;
        currentMousePositionInImage = new Point(mousePosition.x - canvas.getStartX(), mousePosition.y - canvas.getStartY());
    }

    public Point getCurrentMousePositionInCanvas() {
        return currentMousePositionInImage;
    }

    public int getCurrentMousePositionInCanvasX() {
        return currentMousePositionInImage.x;
    }

    public int getCurrentMousePositionInCanvasY() {
        return currentMousePositionInImage.y;
    }

    public Point getPreviousMousePositionInCanvas() {
        return previousMousePositionInImage;
    }

    public int getPreviousMousePositionInCanvasX() {
        return previousMousePositionInImage.x;
    }

    public int getPreviousMousePositionInCanvasY() {
        return previousMousePositionInImage.y;
    }
}
