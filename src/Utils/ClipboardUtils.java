package Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

public class ClipboardUtils {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void copyToClipboard(BufferedImage img) {
        clipboard.setContents(new TransferableImage(img), null);
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
