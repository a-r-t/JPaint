package Tools;

import Canvas.Canvas;
import Canvas.CanvasListener;
import Models.ChoicesHolder;
import Utils.ColorUtils;
import Utils.MouseClick;
import Utils.MouseInfoHolder;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Canvas.CanvasCursor;

public class EyeDropperTool extends BaseTool {
    private ArrayList<CanvasListener> canvasListeners;

    public EyeDropperTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager, ArrayList<CanvasListener> canvasListeners) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);
        this.canvasListeners = canvasListeners;
    }

    @Override
    public void mousePressed() {
        Point mousePosition = mouseInfoHolder.getCurrentMousePositionInImage();
        int rgb = canvas.getMainImage().getRGB(mousePosition.x / choicesHolder.getScale(), mousePosition.y / choicesHolder.getScale());
        Color color = ColorUtils.getColorFromInt(rgb);
        if (mouseInfoHolder.isLeftMouseButtonPressed()) {
            choicesHolder.setPaintColor(color);

            // let subscribers know paint color was just changed
            for (CanvasListener listener : canvasListeners) {
                listener.onPaintColorChanged(color);
            }

            // let subscribers know eye dropper was just used to change paint color
            for (CanvasListener listener : canvasListeners) {
                listener.onEyeDropperUsedToChangePaintColor();
            }
        }
        else if (mouseInfoHolder.isRightMouseButtonPressed()) {
            choicesHolder.setEraseColor(color);

            // let subscribers know erase color was just changed
            for (CanvasListener listener : canvasListeners) {
                listener.onEraseColorChanged(color);
            }
        }
    }

    @Override
    public Cursor getCursor() {
        return cursorManager.get(CanvasCursor.EYE_DROPPER);
    }
}
