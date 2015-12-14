/**
 * 
 */
package tech.bcozo.game.snakegame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

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
    private static final float MOVE_TIME = 0.2F;
    private final String sneakeHeadSrcPath = "snakehead.png";
    private final String sneakeBodySrcPath = "snakebody.png";
    private final String appleSrcPath = "apple.png";
    private static int gridSize = 16;
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

    /**
     * <p>
     * This is the constructor of GameScreen
     * </p>
     */
    public GameScreen() {
        timer = MOVE_TIME;
        snakeX = snakeY = 0;
        snakeDirection = Controller.RIGHT;
        appleAvailable = false;
        appleX = appleY = 0;
        bodyParts = new ArrayList<BodyPart>();
        snakeXBeforeUpdate = snakeYBeforeUpdate = 0;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal(sneakeHeadSrcPath));
        snakeBody = new Texture(Gdx.files.internal(sneakeBodySrcPath));
        apple = new Texture(Gdx.files.internal(appleSrcPath));
    }

    @Override
    public void render(float delta) {
        timer -= delta;
        queryInput();
        if (timer <= 0) {
            timer = MOVE_TIME;
            moveSnake();
            checkForOutOfBounds();
            updateBodyPartsPosition();
        }
        checkAppleCollision();
        checkAndPlaceApple();
        clearScreen();
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
        if (lPressed)
            snakeDirection = Controller.LEFT;
        if (rPressed)
            snakeDirection = Controller.RIGHT;
        if (uPressed)
            snakeDirection = Controller.UP;
        if (dPressed)
            snakeDirection = Controller.DOWN;
    }

    private void moveSnake() {
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

    private void updateBodyPartsPosition() {
        if (bodyParts.size() > 0) {
            BodyPart bodyPart = bodyParts.remove(0);
            bodyPart.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
            bodyParts.add(bodyPart);
        }
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            do {
                appleX = MathUtils.random(
                        Gdx.graphics.getWidth() / gridSize - 1) * gridSize;
                appleY = MathUtils.random(
                        Gdx.graphics.getHeight() / gridSize - 1) * gridSize;
                appleAvailable = true;
            } while (appleX == snakeX && appleY == snakeY);
        }
    }

    private void checkAppleCollision() {
        if (appleAvailable && appleX == snakeX && appleY == snakeY) {
            BodyPart bodyPart = new BodyPart(snakeBody);
            bodyPart.updateBodyPosition(snakeX, snakeY);
            bodyParts.add(0, bodyPart);
            appleAvailable = false;
        }
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
}
