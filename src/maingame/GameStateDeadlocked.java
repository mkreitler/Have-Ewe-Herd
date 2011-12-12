/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
public class GameStateDeadlocked extends GameStateRoundFailed {
    private static final String FAIL_MESSAGE    = "Roundup failed! Click to try again.";

    // Constructors ////////////////////////////////////////////////////////////
    public GameStateDeadlocked(PlayingScene sceneIn, LevelLayout levelIn) {
        super(sceneIn, levelIn);

        currentLevel = levelIn;
    }

    // Public Methods //////////////////////////////////////////////////////////

    // Protected Methods ///////////////////////////////////////////////////////
    @Override
    protected String getFailMessage() { return FAIL_MESSAGE; }

    // Private Methods /////////////////////////////////////////////////////////
}
