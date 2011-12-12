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

public class LvlChatanoogaChooChoo extends LevelSetup{
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
        "     - - - - -   ",
        " 0 0 0 0 0 0 0 0 ",
        "     - - - -     ",
        " 0|0|0 0 0 0|0|0 ",
        "                 ",
        " 0 0 0 S 0 B 0 0 ",
        "       - -       ",
        " M 0 0 0 0 0 0 D ",
        "                 ",
        " 0 0 S S 0 0 S 0 ",
        "                 ",
        " 0 0 0 B 0 0 S 0 ",
        "                 ",
        " 0|0 0 0 0 0 B 0 ",
        "                 ",
        " 0|0 0 0 0 0 0 0 ",
        "                 ",
    };

    public LvlChatanoogaChooChoo() {
        super(GEOMETRY, // Geometry
              10,        // Normal fence segments
              6,        // Electric fence segments
              "Chatanooga choo-choo.", PlayingScene.MESSAGE_ATTENTION, PlayingScene.MESSAGE_TIME_FOREVER,
              "All a-board 'em up!", PlayingScene.MESSAGE_NORMAL, PlayingScene.MESSAGE_TIME_FOREVER,
              100);
    }
}
