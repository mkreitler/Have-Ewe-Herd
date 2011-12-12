/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import pulpcore.Input;

public class GameStateExecute extends GameState {
    public static final int    TICK_INTERVAL_MS    = 500;

    private int         clock;
    private LevelLayout currentLevel    = null;
    private int         tickCount       = 0;

    // Contructors /////////////////////////////////////////////////////////////
    public GameStateExecute(PlayingScene sceneIn, LevelLayout currentLevel) {
        super(sceneIn);

        this.currentLevel = currentLevel;
    }

    // Public Methods //////////////////////////////////////////////////////////
    @Override
    public void enter() {
        clock       = 0;
        tickCount   = 0;

        if (currentLevel != null) {
            currentLevel.startExecution();
        }
    }

    @Override
    public void update(int elapsedTime) {
        clock += elapsedTime;

        while (clock > TICK_INTERVAL_MS) {
            if (Input.isMouseDown()) {
                int debugMe = 0;
            }

            tick();

            clock -= TICK_INTERVAL_MS;
            ++tickCount;
        }
    }

    @Override
    public void exit() {
        if (currentLevel != null) {
            currentLevel.popLambs();
            currentLevel.resetSounds();
        }
    }

    // Private Methods /////////////////////////////////////////////////////////
    /**
     * Move all board elements.
     */
    private void tick() {
        if (currentLevel != null) {
            currentLevel.setAllCrittersAsUnmoved();

            boolean bSomeoneMoved = currentLevel.tick(TICK_INTERVAL_MS, tickCount);

            if (!currentLevel.levelFailed() &&
                !bSomeoneMoved &&
                scene != null) {

                // No critters moved. Enter the "round end" state.
                scene.setState(scene.stateEndRound);
            }
        }
    }
}
