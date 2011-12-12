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

public class LvlTriplePlay extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "       _         ",
        " 0 0 0|L|D 0 0 0 ",
        "     _           ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 S 0 S 0 ",
        "   _             ",
        " 0|L|0 S 0 0 0|L|",
        "               _ ",
        " 0|0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 M 0 0 0 ",
        "                 ",
    };

    public LvlTriplePlay() {
        super(GEOMETRY, // Geometry
              8,        // Normal fence segments
              3,        // Electric fence segments
              "Welcome to \'triple play\'.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "They're outta there!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
