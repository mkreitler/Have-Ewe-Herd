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

public class LvlFourCorners extends LevelSetup{
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
        "     -     - -   ",
        " S 0 0|0 0 0 0 S ",
        "         -       ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "   -             ",
        " 0 0 0 D B S 0 M ",
        "                 ",
        " 0|0 0 0 0 0|0 0 ",
        "                 ",
        " 0|0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0|0 ",
        "         -       ",
        " S 0 0 0 0 0 0 S ",
        "                 ",
    };

    public LvlFourCorners() {
        super(GEOMETRY, // Geometry
              20,        // Normal fence segments
               0,        // Electric fence segments
              "Can you get the all the sheep?", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Kn-ewe you could!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
