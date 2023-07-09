import java.awt.*;

public class ColorUtils {
    public static int getIntFromColor(Color color){
        return color.getRGB(); // the .getRGB method includes alpha
    }

    public static Color getColorFromInt(int rgb){
        return new Color(rgb);
    }
}
