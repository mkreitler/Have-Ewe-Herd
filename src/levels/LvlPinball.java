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

public class LvlPinball extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 D ",
        " . . . . . . . . ",
        ":0 0 0 0 0 0 0 0:",
        "     .   .       ",
        ":0 0:0:0:0:0 S 0:",
        "     .   .       ",
        ":0 0 0 0 0 0 0 0:",
        "       .         ",
        ":0 S 0:0:0 0 0 0:",
        "       .         ",
        ":0 0 0 B 0 0 0 0:",
        "                 ",
        ":0:0 0 0 0 0:0 0:",
        "   .       .     ",
        "|0|0:0|0|0:0|0|M|",
        " _     _     _ _ ",
    };

    public LvlPinball() {
        super(GEOMETRY, // Geometry
              3,        // Normal fence segments
              3,        // Electric fence segments
              "Time for some pinball.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Maybe we should call it \'pen-ball\'.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
