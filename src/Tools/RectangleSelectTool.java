package Tools;

import Canvas.Canvas;
import Canvas.CanvasMouseInfoHolder;
import Canvas.CanvasCursorManager;
import Models.ChoicesHolder;
import Canvas.CanvasCursor;
import Canvas.CanvasListener;

import Utils.*;
import Utils.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RectangleSelectTool extends BaseTool {
    private Mode mode = null;
    private Rectangle selectBorder;
    private BufferedImage selectedSubimage;
    private boolean isSelectedSubimageExternal;
    private Point selectedSubimageOriginalLocation;
    private Point selectedSubimageCurrentLocation;
    private Rectangle originalSelectBorder;
    private Point selectAnchor;
    private ArrayList<CanvasListener> canvasListeners;

    public RectangleSelectTool(Canvas canvas, ChoicesHolder choicesHolder, CanvasMouseInfoHolder mouseInfoHolder, CanvasCursorManager cursorManager, ArrayList<CanvasListener> canvasListeners) {
        super(canvas, choicesHolder, mouseInfoHolder, cursorManager);
        this.canvasListeners = canvasListeners;
        reset();
    }

    @Override
    public void mousePressed() {
        if (mode == null && mouseInfoHolder.isLeftMouseButtonPressed()) {

            // move existing selection
            if (selectBorder.contains(new Point(mouseInfoHolder.getCurrentMousePositionInImageX() / choicesHolder.getScale(), mouseInfoHolder.getCurrentMousePositionInImageY() / choicesHolder.getScale()))) {
                mode = Mode.MOVE;
                //selectedSubimage = canvas.getMainImage().getSubImage(originalSelectBorder.x, originalSelectBorder.y, selectBorder.width + 1, selectBorder.height + 1);
                selectedSubimageOriginalLocation = new Point(originalSelectBorder.x, originalSelectBorder.y);
                selectedSubimageCurrentLocation = new Point(selectBorder.x, selectBorder.y);
            }

            // create new selection
            else {
                mode = Mode.SELECT;

                // if a previous selection exists, permanently apply the changes to the image
                if (selectBorder.width > 0 && selectBorder.height > 0) {
                    applyChanges();
                }

                // prepare for new selection to be made
                canvas.getSelectionImageLayer().clear(new Color(0, 0, 0, 0));
                selectAnchor = new Point(mouseInfoHolder.getCurrentMousePositionInImageX() / choicesHolder.getScale(), mouseInfoHolder.getCurrentMousePositionInImageY() / choicesHolder.getScale());
                canvas.setAllowCanvasResizing(false);
                selectBorder = new Rectangle(0, 0, 0, 0);
                originalSelectBorder = new Rectangle(selectBorder.x, selectBorder.y, selectBorder.width, selectBorder.height);
                canvas.repaint();
            }
        }
    }

    @Override
    public void mouseDragged() {
        if (mode == Mode.SELECT) { // creating a new selection
            int pixelX = mouseInfoHolder.getCurrentMousePositionInImageX() / choicesHolder.getScale();
            int pixelY = mouseInfoHolder.getCurrentMousePositionInImageY() / choicesHolder.getScale();

            int x = selectAnchor.x;
            int y = selectAnchor.y;
            int width = pixelX - selectAnchor.x;
            int height = pixelY - selectAnchor.y;
            if (width < 0) {
                x = pixelX;
                if (x < 0) {
                    x = 0;
                }
                width = selectAnchor.x - x;
            }
            if (height < 0) {
                y = pixelY;
                if (y < 0) {
                    y = 0;
                }
                height = selectAnchor.y - y;
            }

            if (x + width >= canvas.getMainImage().getWidth()) {
                width = canvas.getMainImage().getWidth() - x - 1;
            }
            if (y + height >= canvas.getMainImage().getHeight()) {
                height = canvas.getMainImage().getHeight() - y - 1;
            }


            selectBorder = new Rectangle(x, y, width, height);

            canvas.getSelectionImageLayer().clear(new Color(0, 0, 0, 0));
            Graphics2D graphics = canvas.getSelectionImageLayer().getGraphics();
            graphics.setColor(new Color(0, 0, 0, 255));

            if (selectBorder.width > 0 && selectBorder.height > 0) {
                for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                    graphics.fillRect(i, selectBorder.y, 1, 1);
                    graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
                }
                for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                    graphics.fillRect(selectBorder.x, i, 1, 1);
                    graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
                }

                // this entire block fixes the bottom left of the rectangle select border because it was sometimes coming out wonky due to pixel count
                // it forces the bottom left pixel to always be filled in, and then potentially adjusts the adjacent pixels to make it look less awkward
                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height, 1, 1);
                if (canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height) == ColorUtils.getIntFromColor(Color.black) && canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1) == ColorUtils.getIntFromColor(Color.black)) {
                    graphics.setColor(new Color(0, 0, 0, 0));
                    graphics.setComposite(AlphaComposite.Clear);
                    graphics.fillRect(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height, 1, 1);
                    graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1, 1, 1);
                    graphics.setColor(new Color(0, 0, 0, 255));
                    graphics.setComposite(AlphaComposite.SrcOver);
                    graphics.fillRect(selectBorder.x + selectBorder.width - 2, selectBorder.y + selectBorder.height, 1, 1);
                    graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 2, 1, 1);
                }
            }
            graphics.dispose();
            originalSelectBorder = new Rectangle(selectBorder.x, selectBorder.y, selectBorder.width, selectBorder.height);
        }
        else if (mode == Mode.MOVE) { // moving an existing selection

            int absX = mouseInfoHolder.getCurrentMousePositionX() + canvas.getStartX();
            int absY = mouseInfoHolder.getCurrentMousePositionY() + canvas.getStartY();
            int prvX = mouseInfoHolder.getPreviousMousePositionX() + canvas.getStartX();
            int prvY = mouseInfoHolder.getPreviousMousePositionY() + canvas.getStartY();

            int differenceX = absX / choicesHolder.getScale() - prvX / choicesHolder.getScale();
            int differenceY = absY / choicesHolder.getScale() - prvY / choicesHolder.getScale();

            Graphics2D graphics =  canvas.getSelectionImageLayer().getGraphics();
            canvas.getSelectionImageLayer().clear(new Color(0, 0, 0, 0));


            // if selected sub image was originally a part of the existing image, fill in the original location with the erase color
            // this check is needed to ensure that this will not happen if a subimage is pasted in
            if (!isSelectedSubimageExternal) {
                graphics.setColor(choicesHolder.getEraseColor());
                graphics.fillRect(selectedSubimageOriginalLocation.x, selectedSubimageOriginalLocation.y, selectBorder.width + 1, selectBorder.height + 1);
            }

            selectedSubimageCurrentLocation = new Point(selectedSubimageCurrentLocation.x + differenceX, selectedSubimageCurrentLocation.y + differenceY);

            graphics.drawImage(selectedSubimage, selectedSubimageCurrentLocation.x, selectedSubimageCurrentLocation.y, selectBorder.width + 1, selectBorder.height + 1, null);
            selectBorder = new Rectangle(selectedSubimageCurrentLocation.x, selectedSubimageCurrentLocation.y, selectBorder.width, selectBorder.height);

            graphics.setColor(new Color(0, 0, 0, 255));

            for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                graphics.fillRect(i, selectBorder.y, 1, 1);
                graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
            }
            for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                graphics.fillRect(selectBorder.x, i, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
            }

            // this entire block fixes the bottom left of the rectangle select border because it was sometimes coming out wonky due to pixel count
            // it forces the bottom left pixel to always be filled in, and then potentially adjusts the adjacent pixels to make it look less awkward
            graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height, 1, 1);
            if (selectBorder.x + selectBorder.width < canvas.getMainImage().getWidth() && selectBorder.y + selectBorder.height < canvas.getMainImage().getHeight() && canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height) == ColorUtils.getIntFromColor(Color.black) && canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1) == ColorUtils.getIntFromColor(Color.black)) {
                graphics.setColor(new Color(0, 0, 0, 0));
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1, 1, 1);
                graphics.setColor(new Color(0, 0, 0, 255));
                graphics.setComposite(AlphaComposite.SrcOver);
                graphics.fillRect(selectBorder.x + selectBorder.width - 2, selectBorder.y + selectBorder.height, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 2, 1, 1);
            }
        }
        canvas.repaint();
    }

    @Override
    public void mouseReleased() {
        if (!mouseInfoHolder.isLeftMouseButtonPressed()) {
            // if finished selecting a sub image
            if (mode == Mode.SELECT) {
                if (selectBorder.width == 0 || selectBorder.height == 0) {
                    selectedSubimage = null;
                    canvas.setAllowCanvasResizing(true);
                    canvas.repaint();
                }
                else {
                    canvas.getCanvasHistory().createPerformedState();
                    selectedSubimage = canvas.getMainImage().getSubImage(selectBorder.x, selectBorder.y, selectBorder.width + 1, selectBorder.height + 1);
                    isSelectedSubimageExternal = false;
                }

                // this alerts subscribers that selected sub image has changed
                for (CanvasListener listener: canvasListeners) {
                    listener.onSelectedSubImageChanged(selectedSubimage);
                }
            }
            mode = null;
        }
    }

    @Override
    public Cursor getCursor() {
        if (selectBorder.contains(new Point(mouseInfoHolder.getCurrentMousePositionInImageX() / choicesHolder.getScale(), mouseInfoHolder.getCurrentMousePositionInImageY() / choicesHolder.getScale()))) {
            return cursorManager.get(CanvasCursor.DRAG);
        }
        return cursorManager.get(CanvasCursor.SELECT);
    }

    public void applyChanges() {
        if (originalSelectBorder != null && selectBorder != null && selectedSubimage != null) {
            canvas.getCanvasHistory().createPerformedState();

            Graphics2D graphics = canvas.getMainImage().getGraphics();

            // if selected sub image was originally a part of the existing image, fill in the original location with the erase color
            // this check is needed to ensure that this will not happen if a subimage is pasted in
            if (!isSelectedSubimageExternal) {
                graphics.setColor(choicesHolder.getEraseColor());
                graphics.fillRect(originalSelectBorder.x, originalSelectBorder.y, selectBorder.width + 1, selectBorder.height + 1);
            }

            graphics.drawImage(selectedSubimage, selectBorder.x, selectBorder.y, selectedSubimage.getWidth(), selectedSubimage.getHeight(), null);

            graphics.dispose();
            reset();
        }
        canvas.getSelectionImageLayer().clear(new Color(0, 0, 0, 0));
    }

    @Override
    public void reset() {
        originalSelectBorder = new Rectangle(0, 0, 0, 0);
        selectBorder = new Rectangle(0, 0, 0, 0);
        selectedSubimage = null;
        isSelectedSubimageExternal = false;
        mode = null;

        for (CanvasListener listener: canvasListeners) {
            listener.onSelectedSubImageChanged(selectedSubimage);
        }
    }

    private enum Mode {
        SELECT, MOVE
    }

    public BufferedImage getSelectedSubimage() {
        return selectedSubimage;
    }

    public void setSelectedSubimage(BufferedImage selectedSubimage) {
        applyChanges();
        reset();

        canvas.getCanvasHistory().createPerformedState();

        this.selectedSubimage = selectedSubimage;
        isSelectedSubimageExternal = true;

        selectBorder = new Rectangle(0, 0, selectedSubimage.getWidth() - 1, selectedSubimage.getHeight() - 1);
        canvas.getSelectionImageLayer().clear(new Color(0, 0, 0, 0));
        Graphics2D graphics = canvas.getSelectionImageLayer().getGraphics();

        graphics.drawImage(selectedSubimage, selectBorder.x, selectBorder.y, selectBorder.width + 1, selectBorder.height + 1, null);

        graphics.setColor(new Color(0, 0, 0, 255));
        if (selectBorder.width > 0 && selectBorder.height > 0) {
            for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                graphics.fillRect(i, selectBorder.y, 1, 1);
                graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
            }
            for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                graphics.fillRect(selectBorder.x, i, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
            }

            // this entire block fixes the bottom left of the rectangle select border because it was sometimes coming out wonky due to pixel count
            // it forces the bottom left pixel to always be filled in, and then potentially adjusts the adjacent pixels to make it look less awkward
            graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height, 1, 1);
            if (canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height) == ColorUtils.getIntFromColor(Color.black) && canvas.getSelectionImageLayer().getRGB(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1) == ColorUtils.getIntFromColor(Color.black)) {
                graphics.setColor(new Color(0, 0, 0, 0));
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1, 1, 1);
                graphics.setColor(new Color(0, 0, 0, 255));
                graphics.setComposite(AlphaComposite.SrcOver);
                graphics.fillRect(selectBorder.x + selectBorder.width - 2, selectBorder.y + selectBorder.height, 1, 1);
                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 2, 1, 1);
            }
        }
        graphics.dispose();
        originalSelectBorder = new Rectangle(selectBorder.x, selectBorder.y, selectBorder.width, selectBorder.height);
        canvas.setAllowCanvasResizing(false);
        canvas.repaint();

        for (CanvasListener listener: canvasListeners) {
            listener.onSelectedSubImageChanged(selectedSubimage);
        }
    }
}
