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

public class GameStateRoundFailed extends GameState {
    private static final String FAIL_MESSAGE        = "Lost one! Click to try again.";

    protected LevelLayout currentLevel = null;

    // Constructors ////////////////////////////////////////////////////////////
    public GameStateRoundFailed(PlayingScene sceneIn, LevelLayout levelIn) {
        super(sceneIn);

        currentLevel = levelIn;
    }

    // Public Methods //////////////////////////////////////////////////////////
    @Override
    public void enter() {
        if (scene != null) {
            scene.showMessage(FAIL_MESSAGE, PlayingScene.MESSAGE_ALERT, PlayingScene.MESSAGE_TIME_FOREVER);

            if (currentLevel != null) {
                currentLevel.playSoundBull(true);
            }
        }
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime);

        if (pulpcore.Input.isMousePressed() && scene != null) {

            // Replay this level.
            scene.setState(scene.stateSetup);
        }
    }

    // Protected Methods ///////////////////////////////////////////////////////
    protected String getFailMessage() { return FAIL_MESSAGE; }

    // Private Methods /////////////////////////////////////////////////////////
}
