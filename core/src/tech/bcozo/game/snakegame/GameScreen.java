/**
 * 
 */
package tech.bcozo.game.snakegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
    private static final float MOVE_TIME = 1F;
    private final String sneakeHeadPath = "snakehead.png";
    private static int snakeMovement = 16;
    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private float timer;
    private int snakeX;
    private int snakeY;
    private int snakeDirection;

    /**
     * <p>
     * This is the constructor of GameScreen
     * </p>
     */
    public GameScreen() {
        timer = MOVE_TIME;
        snakeX = snakeY = 0;
        snakeDirection = Controller.RIGHT;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal(sneakeHeadPath));
    }

    @Override
    public void render(float delta) {
        timer -= delta;
        if (timer <= 0) {
            timer = MOVE_TIME;
            snakeX += snakeMovement;
        }
        checkForOutOfBounds();
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b,
                Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(snakeHead, snakeX, snakeY);
        batch.end();

    }

    private void checkForOutOfBounds() {
        if (snakeX >= Gdx.graphics.getWidth()) {
            snakeX = 0;
        } else if (snakeX < 0) {
            snakeX = Gdx.graphics.getWidth() - snakeMovement;
        }
        if (snakeY >= Gdx.graphics.getHeight()) {
            snakeY = 0;
        } else if (snakeY < 0) {
            snakeY = Gdx.graphics.getHeight() - snakeMovement;
        }
    }

    private void moveSnake() {
        switch (snakeDirection) {
        case Controller.UP:
            snakeY += snakeMovement;
            break;
        case Controller.DOWN:
            snakeY -= snakeMovement;
            break;
        case Controller.LEFT:
            snakeX -= snakeMovement;
            break;
        case Controller.RIGHT:
            snakeX += snakeMovement;
            break;
        default:
            break;
        }
    }
}
