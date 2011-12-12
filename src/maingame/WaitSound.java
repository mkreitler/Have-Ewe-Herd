/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maingame;

/**
 *
 * @author mkreitler
 */
import pulpcore.sound.Sound;

public class WaitSound extends Object {
    private static final int DEFAULT_DELAY_MS   = 500;
    private int delayTimer;
    private int delay;
    private Sound sound;

    // Constructors ////////////////////////////////////////////////////////////
    public WaitSound(String soundName) {
        sound = Sound.load(soundName);

        delayTimer  = 0;
        delay       = DEFAULT_DELAY_MS;
    }

    public WaitSound(String soundName, int delayOverride) {
        sound = Sound.load(soundName);

        delayTimer  = 0;
        delay       = delayOverride;
    }

    // Public Methods //////////////////////////////////////////////////////////
    public void reset() {
        delayTimer = 0;
    }

    public void play(boolean bReset) {
        if (bReset) {
            reset();
        }

        if (sound != null && delayTimer == 0) {
            delayTimer = delay;
            sound.play();
        }
    }

    public void update(int elapsedTime) {
        delayTimer = Math.max(delayTimer - elapsedTime, 0);
    }

    // Protected Methods ///////////////////////////////////////////////////////

    // Private Methods /////////////////////////////////////////////////////////

}
