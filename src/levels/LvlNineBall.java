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

public class LvlNineBall extends LevelSetup{
    private static final String[] GEOMETRY = {
        "   _         _   ",
        " 0|0 0 M 0 0 0|0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 S 0 0 0 0 ",
        " _             _ ",
        "|0 0 S B S 0 0 0|",
        " _             _ ",
        " 0 0 0 S 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0|0 0 0 0 0 0|0 ",
        "   _         _   ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
    };

    public LvlNineBall() {
        super(GEOMETRY, // Geometry
              7,        // Normal fence segments
              2,        // Electric fence segments
              "Fancy a bit of 9-ball?", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Nice break!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER);
    }
}
