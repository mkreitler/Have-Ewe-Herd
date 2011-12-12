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
public class LvlTutorial05 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 D 0 0 0 ",
        "     _           ",
        " 0 0|0 0 0|0 0 0 ",
        "     _           ",
        " 0 0 0 0 S 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 M 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlTutorial05() {
        super(GEOMETRY, // Geometry
              1,        // Normal fence segments
              0,        // Electric fence segments
              "If blocked right, sheep turn left.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Ewe know what to do.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
