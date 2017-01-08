package lt.maze.ysf.data;

import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.SampObject;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class ObjectMaterialText {

    private SampObject object;
    private int index;
    private String text;
    private ObjectMaterialSize size;
    private String font;
    private int fontSize;
    private boolean bold;
    private Color fontColor;
    private Color backgroundColor;
    private ObjectMaterialTextAlign textAlign;

    public ObjectMaterialText(SampObject object, int index, String text, ObjectMaterialSize size, String font, int fontSize, boolean bold, Color fontColor, Color backgroundColor, ObjectMaterialTextAlign textAlign) {
        this.object = object;
        this.index = index;
        this.text = text;
        this.size = size;
        this.font = font;
        this.fontSize = fontSize;
        this.bold = bold;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.textAlign = textAlign;
    }

    public SampObject getObject() {
        return object;
    }

    public void setObject(SampObject object) {
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
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

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public ObjectMaterialTextAlign getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(ObjectMaterialTextAlign textAlign) {
        this.textAlign = textAlign;
    }
}
