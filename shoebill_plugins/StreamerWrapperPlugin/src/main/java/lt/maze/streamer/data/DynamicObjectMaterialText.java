package lt.maze.streamer.data;

import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.data.Color;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicObjectMaterialText {

    private String text;
    private ObjectMaterialSize size;
    private String fontFace;
    private int fontSize, alignment;
    private boolean bold;
    // ARGB format
    private Color fontColor, backColor;


    public DynamicObjectMaterialText(String text, ObjectMaterialSize size, String fontFace, int fontSize, int alignment, boolean bold, Color fontColor, Color backColor) {
        this.text = text;
        this.size = size;
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.alignment = alignment;
        this.bold = bold;
        this.fontColor = fontColor;
        this.backColor = backColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ObjectMaterialSize getSize() {
        return size;
    }

    public void setSize(ObjectMaterialSize size) {
        this.size = size;
    }

    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String fontFace) {
        this.fontFace = fontFace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }
}
