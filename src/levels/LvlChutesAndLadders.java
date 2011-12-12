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

public class LvlChutesAndLadders extends LevelSetup{
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0|0|0 0 0 0|0|0 ",
        "                 ",
        " 0|S|0 0 0 0|0|0 ",
        "                 ",
        " 0|S|0 0 0 0|0|0 ",
        "     - - - -     ",
        " 0|S 0|M|0|0 S|D ",
        "     - - - -     ",
        " 0|0|0 0 0 0|S|0 ",
        "                 ",
        " 0|0|0 0 0 0|S|0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlChutesAndLadders() {
        super(GEOMETRY, // Geometry
              4,        // Normal fence segments
              0,        // Electric fence segments
              "Chutes and ladders.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Eats chutes and leaves.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
