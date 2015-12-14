package tech.bcozo.game.snakegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SnakeGame extends Game {
    SpriteBatch batch;
    Texture img;

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
