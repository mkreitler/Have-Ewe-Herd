/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package levels;

import maingame.LevelSetup;
import maingame.PlayingScene;

/**
 *
 * @author mkreitler
 */

public class LvlRicochet extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 M 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "       =         ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 0|0|0 ",
        "                 ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
        " 0|0 0 0 0 S 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        "           -     ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlRicochet() {
        super(GEOMETRY, // Geometry
              8,        // Normal fence segments
              3,        // Electric fence segments
              "Time for a trick shot!", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "The pen is mighter than the sword!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
