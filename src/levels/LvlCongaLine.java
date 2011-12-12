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

public class LvlCongaLine extends LevelSetup{
    private static final String[] GEOMETRY = {
        "   _ _ _ _       ",
        " 0|0 0 0 0|0 0 0 ",
        "                 ",
        " 0|0 0 0 0|0 0 0 ",
        "   _     _       ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " M S S S S S 0 D ",
        "                 ",
        " 0 0 0 0 0 S 0 0 ",
        "                 ",
        " 0 0 0 0 0 S 0 0 ",
        "                 ",
        " 0 0 0 0 0 S 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "         _       ",
    };

    public LvlCongaLine() {
        super(GEOMETRY, // Geometry
              4,        // Normal fence segments
              2,        // Electric fence segments
              "Everybody conga!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "Who would've guessed -- dancing sheep?", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
