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

public class LvlGrandSlam extends LevelSetup{
    private static final String[] GEOMETRY = {
        "       _         ",
        " 0 0 0 L|0 0 0|0 ",
        "       _         ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        " _             _ ",
        "|L 0 S 0 S 0 0 L|",
        " _             _ ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 0 0 M 0 0 0 0 ",
        "       _         ",
        " 0 0 0|L 0 0 0 0 ",
        "       _   _     ",
    };

    public LvlGrandSlam() {
        super(GEOMETRY, // Geometry
              5,        // Normal fence segments
              4,        // Electric fence segments
              "Grand Slam!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              "You knew there had to be one.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
