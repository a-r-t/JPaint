package Toolstrip;

import Utils.ImageButton;

import java.awt.*;

public class ToolButton extends ImageButton {
    protected Tool tool;

    public ToolButton(String imageResource, Tool tool) {
        super(imageResource);
        location = new Point(0, 0);
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 11, image.getHeight() + 11);
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }
}
