package Tools;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Utils.MouseClick;
import Utils.MouseInfoHolder;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasCursor;

public class BucketTool extends BaseTool {

    public BucketTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);
    }

    @Override
    public void mousePressed() {
        int color = 0;
        if (mouseInfoHolder.isLeftMouseButtonPressed()) {
            color = choicesHolder.getPaintColorAsIntRGB();
        }
        else if (mouseInfoHolder.isRightMouseButtonPressed()) {
            color = choicesHolder.getEraseColorAsIntRGB();
        }

        Point mousePosition = mouseInfoHolder.getCurrentMousePositionInImage();
        int oldRgb = canvas.getMainImage().getRGB(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale());
        int newRgb = color;
        if (oldRgb != newRgb) {
            spreadColor(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale(), oldRgb, newRgb);
            canvas.repaint();
        }
    }

    // paint bucket spread logic
    // note: on REALLY large areas, there may be a slight delay due to the amount of computation required
    private void spreadColor(int x, int y, int oldRgb, int newRgb) {
        Queue<Point> spreadQueue = new LinkedList<>();
        spreadQueue.add(new Point(x, y));
        while (!spreadQueue.isEmpty()) {
            Point next = spreadQueue.poll();
            if (next.x >= 0 && next.x < canvas.getMainImage().getWidth() && next.y >= 0 && next.y < canvas.getMainImage().getHeight()) {
                int rgb = canvas.getMainImage().getRGB(next.x, next.y);
                if (rgb == oldRgb) {
                    canvas.getMainImage().setRGB(next.x, next.y, newRgb);
                    spreadQueue.add(new Point(next.x + 1, next.y));
                    spreadQueue.add(new Point(next.x - 1, next.y));
                    spreadQueue.add(new Point(next.x, next.y + 1));
                    spreadQueue.add(new Point(next.x, next.y - 1));
                }
            }
        }
    }

    @Override
    public Cursor getCursor() {
        return cursorManager.get(CanvasCursor.BUCKET);
    }
}
