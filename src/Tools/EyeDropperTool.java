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


public class EyeDropperTool extends BaseTool {
    private ArrayList<CanvasListener> canvasListeners;

    public EyeDropperTool(Canvas canvas, ChoicesHolder choicesHolder, MouseInfoHolder mouseInfoHolder, ArrayList<CanvasListener> canvasListeners) {
        super(canvas, choicesHolder, mouseInfoHolder);
        this.canvasListeners = canvasListeners;
    }

    @Override
    public void mousePressed() {
        int mousePositionX = mouseInfoHolder.getCurrentMousePositionX();
        int mousePositionY = mouseInfoHolder.getCurrentMousePositionY();
        int rgb = canvas.getMainImage().getRGB(mousePositionX / choicesHolder.getScale(), mousePositionY / choicesHolder.getScale());
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
}
