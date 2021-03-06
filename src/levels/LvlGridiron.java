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

public class LvlGridiron extends LevelSetup{
    // D = Daisy
    // M = Max
    // | = vertical wooden fence (up/down)
    // _ = horizontal wooden fence (side-to-side)
    // : = vertical electric fence
    // . = horizontal electric fence
    // S = sheep
    // B = black sheep
    // L = lamb
    private static final String[] GEOMETRY = {
        "                 ",
        "|0 0 0|0 0|0 0 0|",
        "       - -       ",
        "|0 0 0 0|0 0 0 0|",
        " - - - - - - - - ",
        " 0 0 0 D 0 0 0 0 ",
        "                 ",
        " 0 B B B B B B 0 ",
        "                 ",
        " 0 S S S S S S 0 ",
        "                 ",
        " 0 0 0 M 0 0 0 0 ",
        "                 ",
        "|0 0 0|0 0|0 0 0|",
        "       - -       ",
        "|0 0 0 0|0 0 0 0|",
        " - - - - - - - - ",
    };

    public LvlGridiron() {
        super(GEOMETRY, // Geometry
              12,        // Normal fence segments
              2,        // Electric fence segments
              "Fourth and goal.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Win it for the Gipper!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
