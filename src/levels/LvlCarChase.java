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

public class LvlCarChase extends LevelSetup{
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
        " -             - ",
        "|0 0 0 0 0 0 0 0|",
        "   -         -   ",
        " 0|0 0 0 0 0 0|0 ",
        "     -     -     ",
        " 0 0|0 0 0 0|0 0 ",
        "       - -       ",
        " M S 0|0 0|0 D 0 ",
        "                 ",
        " 0 0 0|0 0|0 0 0 ",
        "       - -       ",
        " 0 0|0 S 0 0|0 0 ",
        "     -     -     ",
        " 0|0 0 0 0 0 0|0 ",
        "   -         -   ",
        "|0 0 0 0 0 S 0 0|",
        " -             - ",
    };

    public LvlCarChase() {
        super(GEOMETRY, // Geometry
              6,        // Normal fence segments
              0,        // Electric fence segments
              "In honor of Car Chase on the Vic 20.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Lock 'em in!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
