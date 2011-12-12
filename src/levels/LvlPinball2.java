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

public class LvlPinball2 extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 D ",
        " . . . . . . . . ",
        ":0 0 0 S S 0 0 0:",
        "     .   .       ",
        ":0 0:0:0:0:0 0 0:",
        "     .   .       ",
        ":0 0 S 0 0 0 0 0:",
        "       .         ",
        ":0 0 0:0:S 0 0 0:",
        "       .         ",
        ":0 0 0 0 0 0 0 M:",
        "                 ",
        ":0:0 0 0 0 0:0 0:",
        "   .       .     ",
        "|0|0:0|0|0:0|0|0|",
        " _     _     _ _ ",
    };

    public LvlPinball2() {
        super(GEOMETRY, // Geometry
              2,        // Normal fence segments
              2,        // Electric fence segments
              "Up for more pinball?", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Maybe we should call it \'pen-ball\'.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
