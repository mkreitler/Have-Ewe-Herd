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
public class LvlHeadOnCollision extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 M 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "         _ _     ",
        " 0 0 0 0 0 0 0 0 ",
        "         _ _     ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0|0 B 0 0 0|0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
    };

    public LvlHeadOnCollision() {
        super(GEOMETRY, // Geometry
              5,        // Normal fence segments
              0,        // Electric fence segments
              "Sheep stop if they hit head-on.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "They should've done a ewe-turn!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
