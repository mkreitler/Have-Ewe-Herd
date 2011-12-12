package maingame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mkreitler
 */

public class GameStateSetup extends GameStateFencePlacer {
    private boolean bRetry;

    // Public Methods //////////////////////////////////////////////////////////
    public GameStateSetup(PlayingScene sceneIn, LevelLayout levelIn) {
        super(sceneIn, levelIn);
    }

    @Override
    public void enter() {
        if (currentLevel != null &&
            scene != null) {
            LevelSetup nextSetup = scene.getNextLevelSetup();

            if (nextSetup != null) {
                if (bRetry) {
                    currentLevel.retryLevel();
                }
                else {
                    currentLevel.startLevel();
                    currentLevel.setUp(nextSetup);
                }
            }

            if (currentLevel.skipped())
            {
                currentLevel.clearSkipped();
            }
            else
            {
                currentLevel.playSoundBanjo(true);
            }

            bRetry = false;
        }
    }

    @Override public void onClickedMax() {
        if (scene != null) {
            scene.setState(scene.stateExecute);
        }
    }

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected void onPlacedFence(int groupRow, int groupCol) {
    }

    protected void setRetry() { bRetry = true; }

    // Prvate Methods //////////////////////////////////////////////////////////
}
