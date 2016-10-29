package lt.maze.fader;

import net.gtaun.shoebill.data.Color;

/**
 * @author Bebras
 *         2016.04.05.
 */
class PlayerFade {

    private int currentRed;
    private int currentGreen;
    private int currentBlue;
    private int currentAlpha;
    private int changePerFrameRed;
    private int changePerFrameGreen;
    private int changePerFrameBlue;
    private int changePerFrameAlpha;

    private int framesLeft;
    private int framesTotal;

    private boolean fadeBack;

    public PlayerFade(int currentRed, int currentGreen, int currentBlue, int currentAlpha,
                      int changePerFrameRed, int changePerFrameGreen, int changePerFrameBlue, int changePerFrameAlpha,
                      int framesTotal, boolean fadeBack) {
        this.currentRed = currentRed;
        this.currentGreen = currentGreen;
        this.currentBlue = currentBlue;
        this.currentAlpha = currentAlpha;
        this.changePerFrameRed = changePerFrameRed;
        this.changePerFrameGreen = changePerFrameGreen;
        this.changePerFrameBlue = changePerFrameBlue;
        this.changePerFrameAlpha = changePerFrameAlpha;
        this.framesLeft = framesTotal;
        this.framesTotal = framesTotal;
        this.fadeBack = fadeBack;
    }

    public void fadeOut() {
        currentRed -= changePerFrameRed;
        currentGreen -= changePerFrameGreen;
        currentBlue -= changePerFrameBlue;
        currentAlpha -= changePerFrameAlpha;
    }

    public void fadeIn() {
        currentRed += changePerFrameRed;
        currentGreen += changePerFrameGreen;
        currentBlue += changePerFrameBlue;
        currentAlpha += changePerFrameAlpha;
    }

    public Color getColor() {
        return new Color(currentRed, currentGreen, currentBlue, currentAlpha);
    }

    public int getFramesLeft() {
        return framesLeft;
    }

    public void setFramesLeft(int framesLeft) {
        this.framesLeft = framesLeft;
    }

    public int getFramesTotal() {
        return framesTotal;
    }

    public boolean isFadeBack() {
        return fadeBack;
    }
}
