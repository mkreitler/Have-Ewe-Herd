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

public class LvlThreeByThree extends LevelSetup{
    // D = Daisy
    // M = Max
    // | = vertical wooden fence (up/down)
    // - = horizontal wooden fence (side-to-side)
    // : = vertical electric fence
    // . = horizontal electric fence
    // S = sheep
    // B = black sheep
    // L = lamb
    private static final String[] GEOMETRY = {
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 S 0 0 0 0 0 ",
        "                 ",
        " 0 S 0 0 0 0 0 0 ",
        "                 ",
        " M 0 0 S 0 0 0 D ",
        "                 ",
        " B 0 0 0 0 0 0 0 ",
        "                 ",
        " B 0 0 0 0 0 0 0 ",
        "                 ",
        " B 0 0 0 0 0 0 0 ",
        "                 ",
        " 0 0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlThreeByThree() {
        super(GEOMETRY, // Geometry
              10,        // Normal fence segments
               3,        // Electric fence segments
              "Easier than it looks.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "See what I mean?", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
