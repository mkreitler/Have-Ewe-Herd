/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import static pulpcore.image.Colors.*;

public class GameStateEndRound extends GameStateFencePlacer {
    private static final String CLEAR_MESSAGE       = "Clear! Click to continue...";
    private static final int    LEVEL_CLEAR_DELAY   = 500;

    private boolean bClickToContinue    = false;
    private int     levelClearCountdown = 0;
    private boolean bCountingDown       = false;

    // Constructors ////////////////////////////////////////////////////////////
    public GameStateEndRound(PlayingScene sceneIn, LevelLayout levelIn) {
        super(sceneIn, levelIn);
    }

    // Public Methods //////////////////////////////////////////////////////////
    @Override
    public void enter() {
        if (currentLevel != null) {
            currentLevel.showLevelEndMessage();
        }

        bClickToContinue    = false;
        bCountingDown       = false;
        levelClearCountdown = 0;
    }

    @Override
    public void onClickedMax() {
    }

    @Override
    public void update(int elapsedTime) {
        if (bClickToContinue) {
            // Override input handling after successful stage clear.
            if (pulpcore.Input.isMousePressed() && scene != null) {
                // Update score.
                if (currentLevel != null) {
                    scene.addToScore(currentLevel.getLevelScore() +
                                     currentLevel.getRetryBonus() +
                                     currentLevel.getFenceBonus() +
                                     currentLevel.getLambBonus());

                    currentLevel.resetRetryBonus();
                }

                // Advance to the next level.
                if (scene.chooseNextLevel()) {
                    scene.setState(scene.stateSetup);
                }
                else {
                    // Return to the main menu.
                    scene.quit();
                }
            }
        }
        else if (bCountingDown) {
            levelClearCountdown -= elapsedTime;

            if (levelClearCountdown <= 0) {

                // Level cleared!
                scene.showMessage(CLEAR_MESSAGE, WHITE, PlayingScene.MESSAGE_TIME_FOREVER);
                bClickToContinue = true;

                // Show score results.
                if (scene != null) {
                    if (currentLevel != null) {
                        scene.showResults(currentLevel.getLevelScore(),
                                          currentLevel.getRetryBonus(),
                                          currentLevel.getFenceBonus(),
                                          currentLevel.getLambBonus());
                    }
                    else {
                        scene.showResults(0, 0, 0, 0);
                    }
                }
            }
        }
        else {
            super.update(elapsedTime);
        }
    }

    @Override
    public void exit() {
        if (scene != null) {
            scene.hideResults();
        }

        super.exit();
    }

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected void onPlacedFence(int groupRow, int groupCol) {
        if (currentLevel != null && scene != null) {
            if (currentLevel.checkForFencedPlots()) {

                // Start level clear countdown.
                levelClearCountdown = LEVEL_CLEAR_DELAY;
                bCountingDown       = true;

                // Hide fence cursor.
                removeCursor();
            }
        }
    }

    // Private Methods /////////////////////////////////////////////////////////
}
