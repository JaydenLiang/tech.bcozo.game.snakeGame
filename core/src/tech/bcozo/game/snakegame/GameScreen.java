/**
 * 
 */
package tech.bcozo.game.snakegame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import tech.bcozo.game.console.Controller;
import tech.bcozo.game.console.ScreenText;
import tech.bcozo.game.console.ScreenText.ScreenTextAlignment;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: GameScreen
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 13, 2015 10:16:41 PM
 */
public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    private static final float MOVE_TIME = 0.1F;
    private static final String GAME_TITLE_TEXT = "This Snake Game is AWESOME!";
    private static final String GAME_OVER_TEXT = "Game Over!!... Tap space to restart!";
    private static final int POINTS_PER_APPLE = 20;

    private boolean debugMode = true;

    private final String sneakeHeadSrcPath = "snakehead.png";
    private final String sneakeBodySrcPath = "snakebody.png";
    private final String appleSrcPath = "apple.png";
    private static int gridSize = 16;
    private int totalGrids;
    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private Texture apple;
    private float timer;
    private int snakeX;
    private int snakeY;
    private int snakeDirection;
    private boolean appleAvailable;
    private int appleX;
    private int appleY;
    private ArrayList<BodyPart> bodyParts;
    private int snakeXBeforeUpdate;
    private int snakeYBeforeUpdate;
    private int snakeLength;
    private ShapeRenderer shapeRenderer;
    private int high_score;
    private int score;

    private STATE state;
    private BitmapFont bitmapFont;
    private GlyphLayout layout;
    private ScreenText screenText;
    private Viewport viewport;
    private Camera camera;

    /**
     * <p>
     * This is the constructor of GameScreen
     * </p>
     */
    public GameScreen() {
        totalGrids = (int) (WORLD_WIDTH * WORLD_HEIGHT / (gridSize * gridSize));
        state = STATE.PLAYING;
        timer = MOVE_TIME;
        high_score = score = 0;
        snakeX = snakeY = 0;
        snakeLength = 1;
        snakeDirection = Controller.RIGHT;
        appleAvailable = false;
        appleX = appleY = 0;
        bodyParts = new ArrayList<BodyPart>();
        snakeXBeforeUpdate = snakeYBeforeUpdate = 0;

        screenText = new ScreenText();
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        layout = new GlyphLayout();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        // debugSnake();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal(sneakeHeadSrcPath));
        snakeBody = new Texture(Gdx.files.internal(sneakeBodySrcPath));
        apple = new Texture(Gdx.files.internal(appleSrcPath));
        screenText.setScreenSize(viewport.getWorldWidth(),
                viewport.getWorldHeight());
        screenText.setTextLine((Gdx.graphics.getWidth() - layout.width) / 2,
                (4 * Gdx.graphics.getHeight() / 5) - layout.height / 2);
        screenText.setSingleLineSpace();
    }

    @Override
    public void render(float delta) {
        switch (state) {
        case PLAYING:
            timer -= delta;
            queryInput();
            if (timer <= 0) {
                timer = MOVE_TIME;
                moveSnake();
                checkForOutOfBounds();
                updateBodyPartsPosition();
                checkBodyPartsCollision();
            }
            checkAppleCollision();
            checkAndPlaceApple();
            break;
        case GAME_OVER:
            checkForRestart();
            break;
        default:
            break;
        }
        clearScreen();
        drawGrid();
        draw();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b,
                Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.begin();
        batch.draw(snakeHead, snakeX, snakeY);
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (appleAvailable) {
            batch.draw(apple, appleX, appleY);
        }
        if (state == STATE.GAME_OVER) {
            screenText.resetTextLine();
            screenText.addLine(GAME_OVER_TEXT, batch, bitmapFont, layout,
                    ScreenTextAlignment.CENTER);
            screenText.addLineBelow(
                    "High Score: " + Integer.toString(high_score), batch,
                    bitmapFont, layout, ScreenTextAlignment.CENTER);
            screenText.addLineBelow("Score: " + Integer.toString(score), batch,
                    bitmapFont, layout, ScreenTextAlignment.CENTER);
        } else {
            drawScore();
        }
        batch.end();
    }

    private void checkForOutOfBounds() {
        if (snakeX >= Gdx.graphics.getWidth()) {
            snakeX = 0;
        } else if (snakeX < 0) {
            snakeX = Gdx.graphics.getWidth() - gridSize;
        }
        if (snakeY >= Gdx.graphics.getHeight()) {
            snakeY = 0;
        } else if (snakeY < 0) {
            snakeY = Gdx.graphics.getHeight() - gridSize;
        }
    }

    private void queryInput() {
        boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (lPressed && (snakeLength == 1
                || (snakeLength > 1 && snakeDirection != Controller.RIGHT)))
            snakeDirection = Controller.LEFT;
        else if (rPressed && (snakeLength == 1
                || (snakeLength > 1 && snakeDirection != Controller.LEFT)))
            snakeDirection = Controller.RIGHT;
        else if (uPressed && (snakeLength == 1
                || (snakeLength > 1 && snakeDirection != Controller.DOWN)))
            snakeDirection = Controller.UP;
        else if (dPressed && (snakeLength == 1
                || (snakeLength > 1 && snakeDirection != Controller.UP)))
            snakeDirection = Controller.DOWN;
    }

    private void moveSnake() {
        if (state == STATE.PLAYING) {
            snakeXBeforeUpdate = snakeX;
            snakeYBeforeUpdate = snakeY;
            switch (snakeDirection) {
            case Controller.UP:
                snakeY += gridSize;
                break;
            case Controller.DOWN:
                snakeY -= gridSize;
                break;
            case Controller.LEFT:
                snakeX -= gridSize;
                break;
            case Controller.RIGHT:
                snakeX += gridSize;
                break;
            default:
                break;
            }
        }
        // checkBodyPartsCollision();
    }

    private void updateBodyPartsPosition() {
        if (bodyParts.size() > 0) {
            BodyPart bodyPart = bodyParts.remove(0);
            bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
            bodyParts.add(bodyPart);
        }
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            boolean placeValid = true;
            do {
                if (debugMode && placeValid) {
                    switch (snakeDirection) {
                    case Controller.UP:
                        appleX = snakeX;
                        appleY = snakeY + gridSize * 3;
                        if (appleY >= Gdx.graphics.getHeight())
                            appleY -= Gdx.graphics.getHeight();
                        break;
                    case Controller.RIGHT:
                        appleX = snakeX + gridSize * 3;
                        appleY = snakeY;
                        if (appleX >= Gdx.graphics.getWidth())
                            appleX -= Gdx.graphics.getWidth();
                        break;
                    case Controller.DOWN:
                        appleX = snakeX;
                        appleY = snakeY - gridSize * 3;
                        if (appleY < 0)
                            appleY += Gdx.graphics.getHeight();
                        break;
                    case Controller.LEFT:
                        appleX = snakeX - gridSize * 3;
                        appleY = snakeY;
                        if (appleX < 0)
                            appleX += Gdx.graphics.getWidth();
                        break;
                    default:
                        break;
                    }
                } else {
                    appleX = MathUtils.random(
                            Gdx.graphics.getWidth() / gridSize - 1) * gridSize;
                    appleY = MathUtils.random(
                            Gdx.graphics.getHeight() / gridSize - 1) * gridSize;
                }
                placeValid = checkApplePlaceValidity();
            } while (!placeValid);
            appleAvailable = true;
        }
    }

    private boolean checkApplePlaceValidity() {
        if (appleX == snakeX && appleY == snakeY)
            return false;
        else if (snakeLength >= 1 && snakeLength < totalGrids) {
            for (BodyPart bodyPart : bodyParts) {
                if (bodyPart.x == appleX && bodyPart.y == appleY) {
                    return false;
                }
            }
        } else {
            state = STATE.GAME_OVER;
            return true;
        }
        return true;
    }

    private void checkAppleCollision() {
        if (appleAvailable && appleX == snakeX && appleY == snakeY) {
            BodyPart bodyPart = new BodyPart(snakeBody);
            bodyPart.updateBodyPosition(snakeX, snakeY);
            bodyParts.add(0, bodyPart);
            appleAvailable = false;
            snakeLength = bodyParts.size();
            addToScore();
        }
    }

    private void drawScore() {
        if (state == STATE.PLAYING) {
            String scoreString = Integer.toString(score);
            screenText.addLine(scoreString, batch, bitmapFont, layout,
                    ScreenTextAlignment.CENTER);
            layout.setText(bitmapFont, scoreString);
        }
    }

    private void checkBodyPartsCollision() {
        if (snakeLength > 1) {
            for (BodyPart bodyPart : bodyParts) {
                if (bodyPart.x == snakeX && bodyPart.y == snakeY) {
                    state = STATE.GAME_OVER;
                    break;
                }
            }
        }
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeType.Line);
        for (int i = 0; i < Gdx.graphics.getWidth(); i += gridSize) {
            for (int j = 0; j < Gdx.graphics.getHeight(); j += gridSize) {
                shapeRenderer.rect(i, j, gridSize, gridSize);
            }
        }
        shapeRenderer.end();
    }

    private void checkForRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
            restart();
    }

    private void restart() {
        state = STATE.PLAYING;
        timer = MOVE_TIME;
        score = 0;
        snakeLength = 1;
        appleAvailable = false;
        bodyParts.clear();
        snakeXBeforeUpdate = snakeX;
        snakeYBeforeUpdate = snakeY;
        screenText.setTextLine((Gdx.graphics.getWidth() - layout.width) / 2,
                (4 * Gdx.graphics.getHeight() / 5) - layout.height / 2);
    }

    private void addToScore() {
        score += POINTS_PER_APPLE;
        if (score > high_score)
            high_score = score;
    }

    private class BodyPart {
        private int x, y;
        private Texture texture;

        public BodyPart(Texture texture) {
            this.texture = texture;
        }

        public void updateBodyPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Batch batch) {
            if (!(x == snakeX && y == snakeY))
                batch.draw(texture, x, y);
        }
    }

    private enum STATE {
        PLAYING, GAME_OVER
    }
}
