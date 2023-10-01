package Canvas;

import Toolstrip.ToolStrip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class CanvasCursorManager {

    private HashMap<CanvasCursor, Cursor> cursors = new HashMap<>();

    public CanvasCursorManager() {
        loadCursors();
    }

    public Cursor get(CanvasCursor cursor) {
        return cursors.getOrDefault(cursor, Cursor.getDefaultCursor());
    }

    private void loadCursors() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor pencilCursor = null;
        Cursor bucketCursor = null;
        Cursor eyedropperCursor = null;
        Cursor selectCursor = null;
        Cursor dragCursor = null;
        Cursor invisibleCursor = null;
        Cursor resizeHCursor = null;
        Cursor resizeVCursor = null;
        Cursor resizeDRCursor = null;
        try {
            pencilCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/pencil-cursor-transparent.png")),
                    new Point(7, 23),
                    "pencil"
            );
            bucketCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/bucket-cursor-transparent.png")),
                    new Point(8, 19),
                    "bucket"
            );
            eyedropperCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/eyedropper-cursor-transparent.png")),
                    new Point(9, 22),
                    "eyedropper"
            );
            selectCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/select-cursor-transparent.png")),
                    new Point(15, 15),
                    "select"
            );
            dragCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/drag-cursor-transparent.png")),
                    new Point(16, 15),
                    "drag"
            );
            BufferedImage transparentImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            invisibleCursor = toolkit.createCustomCursor(
                    transparentImage,
                    new Point(0, 0),
                    "invisible"
            );
            resizeHCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/resize-h-cursor-transparent.png")),
                    new Point(16, 15),
                    "resizeH"
            );
            resizeVCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/resize-v-cursor-transparent.png")),
                    new Point(15, 15),
                    "resizeV"
            );
            resizeDRCursor = toolkit.createCustomCursor(
                    ImageIO.read(CanvasCursorManager.class.getResource("/resize-dr-cursor-transparent.png")),
                    new Point(16, 13),
                    "resizeDR"
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to read in custom cursors!");
            System.exit(1);
        }
        cursors.put(CanvasCursor.PENCIL, pencilCursor);
        cursors.put(CanvasCursor.BUCKET, bucketCursor);
        cursors.put(CanvasCursor.EYE_DROPPER, eyedropperCursor);
        cursors.put(CanvasCursor.SELECT, selectCursor);
        cursors.put(CanvasCursor.DRAG, dragCursor);
        cursors.put(CanvasCursor.INVISIBLE, invisibleCursor);
        cursors.put(CanvasCursor.E_RESIZE_CURSOR, resizeHCursor);
        cursors.put(CanvasCursor.S_RESIZE_CURSOR, resizeVCursor);
        cursors.put(CanvasCursor.SE_RESIZE_CURSOR, resizeDRCursor);
    }
}
