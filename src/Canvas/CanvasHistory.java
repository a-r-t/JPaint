package Canvas;

import Utils.ImageUtils;

import java.util.*;

public class CanvasHistory {
    private List<CanvasState> performed; // items that have already been performed (undo)
    private List<CanvasState> recall;    // items that have been undone that can be redone (redo)
    private Canvas canvas;

    public CanvasHistory(Canvas canvas) {
        this.canvas = canvas;
        performed = new LinkedList<CanvasState>();
        recall = new LinkedList<CanvasState>();
    }

    public void createPerformedState() {
        CanvasState canvasState = new CanvasState(ImageUtils.copyImage(canvas.getMainImage().getRaw()), canvas.getCanvasWidth(), canvas.getCanvasHeight());
        performed.add(0, canvasState);
        if (performed.size() > 30) {
            performed.remove(performed.size() - 1);
        }
        recall.clear();
    }

    public boolean canUndo() {
        return performed.size() > 0;
    }

    public boolean canRedo() {
        return recall.size() > 0;
    }

    public void undo() {
        if (!performed.isEmpty()) {
            CanvasState lastPerformedCanvasState = performed.remove(0);
            CanvasState currentCanvasState = new CanvasState(ImageUtils.copyImage(canvas.getMainImage().getRaw()), canvas.getCanvasWidth(), canvas.getCanvasHeight());
            setState(lastPerformedCanvasState);
            recall.add(0, currentCanvasState);
        }
    }

    public void redo() {
        if (!recall.isEmpty()) {
            CanvasState recallCanvasState = recall.remove(0);
            CanvasState currentCanvasState = new CanvasState(ImageUtils.copyImage(canvas.getMainImage().getRaw()), canvas.getCanvasWidth(), canvas.getCanvasHeight());
            setState(recallCanvasState);
            performed.add(0, currentCanvasState);
        }
    }

    private void setState(CanvasState canvasState) {
        canvas.setMainImage(canvasState.getMainImage());
        canvas.resizeCanvas(canvasState.getWidth(), canvasState.getHeight());
        canvas.repaint();
    }


}
