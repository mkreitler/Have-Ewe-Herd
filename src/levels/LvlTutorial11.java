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

public class LvlTutorial11 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 M 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0|L|0 0 0 0 ",
        "       _         ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlTutorial11() {
        super(GEOMETRY, // Geometry
              1,        // Normal fence segments
              0,        // Electric fence segments
              "You get a bonus when ewes stops by lambs.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "\'Lamb-tastic\' job!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
