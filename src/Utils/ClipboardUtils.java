package Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

// allow for copying/pasting images to and from the clipboard
public class ClipboardUtils {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private static boolean isClipboardContentAValidImage() {
        Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        // no content in clipboard
        if (content == null) {
            return false;
        }

        // content in clipboard is not able to be converted into an image
        if (!content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return false;
        }

        // there is technically still the possibility that the clipboard content isn't a valid image, but the above checks are enough for 99.9% of cases
        return true;
    }

    public static void copyToClipboard(BufferedImage img) {
        clipboard.setContents(new TransferableImage(img), null);
    }

    public static BufferedImage copyToImage() {
        Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (!isClipboardContentAValidImage()) {
            return null;
        }

        BufferedImage img = null;
        try {
            img = (BufferedImage) content.getTransferData(DataFlavor.imageFlavor);
        } catch (Exception e) {
            try {
                // if clipboard data is not easily converted into a buffered image, first put into standard awt Image and then try converting it
                java.awt.Image temp = (java.awt.Image) content.getTransferData(DataFlavor.imageFlavor);
                img = ImageUtils.convertImageToBufferedImage(temp);
            } catch (Exception e2) {
                return null;
            }
        }
        return img;
    }

    private static class TransferableImage implements Transferable {

        private BufferedImage i;

        public TransferableImage(BufferedImage i) {
            this.i = i;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }
    }
}
