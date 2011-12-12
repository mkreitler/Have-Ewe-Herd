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
public class LvlTutorial10 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 D 0 0 0 0 0 ",
        "   _ _ _         ",
        " 0|0 0 0|0 0 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 S 0 0 0 0 0 ",
        "                 ",
        " 0 0 M 0 0 0 0 0 ",
        "                 ",
    };

    public LvlTutorial10() {
        super(GEOMETRY, // Geometry
              3,        // Normal fence segments
              0,        // Electric fence segments
              "Stopped sheep follow moving friends.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "That last joke was pretty bad.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
