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
public class LvlJailBreak extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "   _   _   _     ",
        " 0|S|0|S|0|S|0 0 ",
        "     _   _       ",
        " M|0|0|0|0|0:0 D ",
        "   _   _   _     ",
        " 0 0|S|0|S|0:0 0 ",
        "     _   _ .     ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlJailBreak() {
        super(GEOMETRY, // Geometry
              3,        // Normal fence segments
              2,        // Electric fence segments
              "Jail break!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Send 'em to the state pen!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
