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

public class LvlEweTurn extends LevelSetup{
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
        " 0 0 0 0 0 0 0 0 ",
        " - -   -         ",
        "|0 0 0 0|0 0 0 0 ",
        "     -           ",
        "|0 M B 0 D 0 0 0 ",
        "                 ",
        "|0 0 B 0 B 0 0 0 ",
        " - -             ",
        " 0 0 B 0 B 0|0 0 ",
        "                 ",
        " 0 0 0 0 B 0|0 0 ",
        "                 ",
        " 0 0 S S S 0 0 0 ",
        "   - -           ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlEweTurn() {
        super(GEOMETRY, // Geometry
               9,        // Normal fence segments
               5,        // Electric fence segments
              "Better make a ewe-turn.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "Looks like you got it sorted.", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
