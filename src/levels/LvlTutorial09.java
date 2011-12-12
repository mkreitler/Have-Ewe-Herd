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
public class LvlTutorial09 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 D 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "     _           ",
        " 0 0|0 0 S:0 0 0 ",
        "     _           ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 M 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlTutorial09() {
        super(GEOMETRY, // Geometry
              1,        // Normal fence segments
              1,        // Electric fence segments
              "Shift-click to build electric fences.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "That sheep went to 'pen state'.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
