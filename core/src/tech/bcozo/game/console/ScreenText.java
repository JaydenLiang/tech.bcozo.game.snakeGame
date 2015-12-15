package tech.bcozo.game.console;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenText {
    private float screenW;
    private float screenH;
    private float textX;
    private float textY;
    private float newTextX;
    private float newTextY;
    private float lineSpace;
    private boolean singleLineSpace = false;
    private boolean doubleLineSpace = false;

    public ScreenText() {
        screenW = 0;
        screenH = 0;
        textX = 0;
        textY = 0;
        newTextX = 0;
        newTextY = 0;
        lineSpace = 1;
    }

    public ScreenText(float screenWidth, float screenHeight, float atX,
            float atY) {
        setScreenSize(screenWidth, screenHeight);
        setTextLine(atX, atY);
        setFixLineSpace(1);
    }

    public ScreenText(float screenWidth, float screenHeight, float atX,
            float atY, float lineSpace) {
        setScreenSize(screenWidth, screenHeight);
        setTextLine(atX, atY);
        setFixLineSpace(lineSpace);
    }

    public static enum ScreenTextAlignment {
        DEFAULT, CENTER, LEFT, RIGHT
    }

    public void setScreenSize(float screenWidth, float screenHeight) {
        screenW = screenWidth;
        screenH = screenHeight;
    }

    public void setTextLine(float atX, float atY) {
        textX = atX;
        textY = atY;
        newTextX = textX;
        newTextY = textY;
    }

    public void setFixLineSpace(float space) {
        lineSpace = space;
        singleLineSpace = false;
        doubleLineSpace = false;
    }

    public void setSingleLineSpace() {
        singleLineSpace = true;
        doubleLineSpace = false;
    }

    public void setDoubleLineSpace() {
        singleLineSpace = false;
        doubleLineSpace = true;
    }

    public void resetTextLine() {
        newTextX = textX;
        newTextY = textY;
    }

    public void addLine(CharSequence str, SpriteBatch batch,
            BitmapFont bitmapFont, GlyphLayout layout,
            ScreenTextAlignment alignment) {
        float alignedTextX = 0;
        float alignedTextY = 0;
        layout.setText(bitmapFont, str);
        switch (alignment) {
        case CENTER:
            alignedTextX = (screenW - layout.width) / 2;
            alignedTextY = newTextY;
            break;
        case LEFT:
            alignedTextX = newTextX;
            alignedTextY = newTextY;
            break;
        case RIGHT:
            alignedTextX = screenW - layout.width;
            alignedTextY = newTextY;
            break;
        default:
            alignedTextX = newTextX;
            alignedTextY = newTextY;
            break;
        }
        bitmapFont.draw(batch, str, alignedTextX, alignedTextY);
    }

    private void adjustLineSpace(float lineHeight) {
        if (doubleLineSpace) {
            newTextY -= lineHeight * 3;
        } else if (singleLineSpace) {
            newTextY -= lineHeight * 2;
        } else {
            newTextY -= lineHeight + lineSpace;
        }
    }

    public void addLineBelow(CharSequence str, SpriteBatch batch,
            BitmapFont bitmapFont, GlyphLayout layout,
            ScreenTextAlignment alignment) {
        adjustLineSpace(layout.height);
        addLine(str, batch, bitmapFont, layout, alignment);
    }
}
